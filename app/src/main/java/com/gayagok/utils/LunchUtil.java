package com.gayagok.utils;

import android.content.Context;
import android.util.Log;

import com.gayagok.models.LunchItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class LunchUtil {

    private final String[] week = {"일", "월", "화", "수", "목", "금", "토"};
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();

    private final SharedPreferenceUtil sharedPreferenceUtil;
    private final ScheduleUtil scheduleUtil;
    private final Gson gson;

    private LunchListener lunchListener;

    public LunchUtil(Context context) {
        numberFormat.setMinimumIntegerDigits(2);
        sharedPreferenceUtil = new SharedPreferenceUtil(context);
        scheduleUtil = new ScheduleUtil(context);
        gson = new Gson();
    }

    public String getStringMenu(Calendar calendar) {
        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {

            String key = getKey(calendar);
            String lunchMenu = "여기를 클릭해 급식을 다운로드해주세요!";

            HashMap<String, LunchItem> lunchList = getList(calendar);
            if (lunchList != null && lunchList.containsKey(key)) {
                lunchMenu = lunchList.get(key)
                        .menu
                        .replaceAll("<font color=#212121>", "")
                        .replaceAll("<font color=#757575>", "")
                        .replaceAll("</font>", "")
                        .replaceAll("<br>",
                                "\n")
                        .replaceAll("\n$", "");
                //오늘 메뉴를 가져오고 앞에 색상정보를 다 제거.
                if (lunchMenu.lastIndexOf("\n") != -1) {
                    lunchMenu = lunchMenu.substring(0, lunchMenu.lastIndexOf("\n")).replaceAll("\n$", "");
                } else {
                    ArrayList<String> list = scheduleUtil.getList(calendar);
                    if (list != null && !list.get(0).isEmpty()) {
                        for (String i : list) {
                            String j[] = i.split(" : ");
                            if (j[0].equals(numberFormat.format(calendar.get(Calendar.DAY_OF_MONTH))) && j[1].contains("방학")) {
                                return "방학입니다.";
                            }
                        }
                    }
                }
            }
            return lunchMenu;
        } else if (getList(calendar) == null) {
            return "여기를 클릭해 급식을 다운로드해주세요!";
        }
        return "급식정보가 없습니다.";
    }

    public HashMap<String, LunchItem> getList(Calendar calendar) {
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String lunch = sharedPreferenceUtil.getString("lunch_" + getKey(calendar), "");
        if (!lunch.isEmpty()) {
            return gson.fromJson(lunch, new TypeToken<HashMap<String, LunchItem>>() {
            }.getType());
        }
        return null;
    }

    public String getKey(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "." + numberFormat.format(calendar.get(Calendar.MONTH) + 1) + "." + numberFormat.format(calendar.get(Calendar.DAY_OF_MONTH)) + "(" + week[calendar.get(Calendar.DAY_OF_WEEK) - 1] + ")";
    }

    public void parseLunch(LunchListener lunchListener, Calendar calendar) {
        this.lunchListener = lunchListener;
        new ParseLunch(calendar).start();
    }

    private class ParseLunch extends Thread {

        final Calendar calendar;

        private ParseLunch(Calendar calendar) {
            this.calendar = calendar;
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }

        @Override
        public void run() {
            try {
                    HashMap<String, LunchItem> items = new HashMap<>();
                    Elements doc = Jsoup.connect("https://stu.cne.go.kr/sts_sci_md01_001.do?schulCode=N100000440&schulCrseScCode=2&schulKndScCode=02&schMmealScCode=2&schYmd=" + calendar.get(Calendar.YEAR) + "." + numberFormat.format(calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.DAY_OF_MONTH))
                            .timeout(5000)
                            .get()
                            .select("table.tbl_type3");
                    //https://stu.cne.go.kr/sts_sci_md01_001.do?schulCode=N100000440&schulCrseScCode=2&schulKndScCode=02&schMmealScCode=2&schYmd=2018.07.28이런식으로 급식파싱

                    Elements day = doc.select("thead tr th[scope=col]");
                    Elements people = doc.select("tbody tr").get(0).select("td.textC");
                    Elements menu = doc.select("tbody tr").get(1).select("td.textC");
                    Elements[] nutrients = new Elements[10];

                    for (int i = 0; i < 10; i++) {
                        nutrients[i] = doc.select("tbody tr").get(doc.select("tbody tr").size() - 10 + i).select("td.textC");
                    }

                    for (int i = 1; i < 6; i++) { //주말 제외 일주일
                        String[] nutrient = new String[10];

                        if (!people.get(i).text().isEmpty()) {//급식인원이 0이 아니면
                            for (int x = 0; x < 10; x++) { //10개 영양소 정보담음
                                nutrient[x] = nutrients[x].get(i).text();
                            }
                            items.put(day.get(i).text(), new LunchItem(day.get(i).text(), menu.get(i).text(), people.get(i).text(), gson.toJson(nutrient), nutrient[0]));
                        } else { //0이면 급식정보 없다함
                            items.put(day.get(i).text(), new LunchItem(day.get(i).text(), "급식정보가 없습니다.", "0명", gson.toJson(nutrient), ""));
                        }
                    }

                    sharedPreferenceUtil.putString("lunch_" + getKey(calendar), gson.toJson(items));
                    lunchListener.onLoadSuccess();
            } catch (Exception e) {
                lunchListener.onLoadError();
            }
        }
    }

}
