package com.gayagok.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleUtil {

    private final SharedPreferenceUtil sharedPreferenceUtil;
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();

    public ScheduleUtil(Context context) {
        sharedPreferenceUtil = new SharedPreferenceUtil(context, "schedule");
        numberFormat.setMinimumIntegerDigits(2);
    }

    public ArrayList<String> getList(Calendar calendar) {
        String schedule = getSchedule(calendar);

        if (!schedule.equals("null")&&!schedule.equals("[]")) {
            return new Gson().fromJson(schedule, ArrayList.class);
        } else if(schedule.equals("null")){
            return null;
        } else {
            ArrayList<String> i = new ArrayList<>();
            i.add("");
            return i;
        }
    }

    public void clearSchedule(Calendar calendar){
        String key = calendar.get(Calendar.YEAR) + numberFormat.format(calendar.get(Calendar.MONTH) + 1);
        sharedPreferenceUtil.putString(sharedPreferenceUtil.getString("mode", "ele") + "_contents_" + key, "[]");
    }

    public String getSchedule(Calendar calendar) {
        String key = calendar.get(Calendar.YEAR) + numberFormat.format(calendar.get(Calendar.MONTH) + 1);

        return sharedPreferenceUtil.getString(sharedPreferenceUtil.getString("mode", "ele") + "_contents_" + key, "null");
    }

    public ArrayList<CalendarDay> getDateList(Calendar calendar) {
        ArrayList<CalendarDay> date = new ArrayList<>();
        ArrayList<String> contents = getList(calendar);

        if(contents != null && !contents.get(0).isEmpty()) {
            for (String i : contents) {
                CalendarDay calendarDay = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, Integer.parseInt(i.substring(0, 2)));
                date.add(calendarDay);
            }
        }

        return date;
    }

    public void parseSchedule(Calendar calendar, ScheduleListener listener) {
            new ParseSchdeule(calendar, listener).start();
    }

    public class ParseSchdeule extends Thread {

        final Calendar calendar;
        final ScheduleListener listener;
        public ParseSchdeule(Calendar calendar, ScheduleListener listener){
            this.calendar = calendar;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                int year = calendar.get(Calendar.YEAR);
                String month = numberFormat.format(calendar.get(Calendar.MONTH) + 1);
                String mode = sharedPreferenceUtil.getString("mode", "ele");
                String uri = "https://stu.cne.go.kr/sts_sci_sf01_001.do?schulCode=N100000440&schulCrseScCode=2&schulKndScCode=02&ay=";

                if (mode.equals("mid")) {
                    uri = "https://stu.cne.go.kr/sts_sci_sf01_001.do?schulCode=N100000420&schulCrseScCode=3&schulKndScCode=03&ay=";
                }

                Elements doc = Jsoup.connect(uri + year + "&mm=" + month)
                        .timeout(5000)
                        .get()
                        .select("div.sub_con table tr");
                ArrayList<String> contents = new ArrayList<>();
                ArrayList<CalendarDay> dates = new ArrayList<>();

                for (Element element : doc) {
                    Elements element1 = element.select("td");
                    for (Element element2 : element1) {
                        String content = element2.select("a strong").text();
                        String date = element2.select("em").text();

                        if (!content.isEmpty()) {
                            if ((!((0 == (year % 4) &&
                                    0 != (year % 100)) ||
                                    0 == year % 400) && Integer.parseInt(month) == 2 && Integer.parseInt(date) == 29) || content.equals("토요휴업일")) { //평년인데 2월 29일이면 건너뜀.
                                continue;
                            }

                            CalendarDay day = CalendarDay.from(year, Integer.parseInt(month), Integer.parseInt(date));
                            dates.add(day);
                            contents.add(date + " : " + content);
                        }
                    }
                }
                sharedPreferenceUtil.putString(mode + "_contents_" + year + month, new Gson().toJson(contents)); //년월로 저장
                listener.onLoadSuccess(contents, dates);
            } catch (IOException e) {
                listener.onLoadError();
                e.printStackTrace();
            }
        }
    }

}
