package com.gayagok.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;

import com.gayagok.views.TimetableListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;

public class TimetableUtil {

    private final Context context;
    private final Calendar calendar = Calendar.getInstance();

    private String[][][] timetable = new String[5][8][2];
    private final String[] middle = {"8:50 - 9:35", "9:45 - 10:30", "10:40 - 11:25", "11:35 - 12:20", "13:10 - 13:55", "14:05 - 14:50", "15:05 - 15:50", "16:00 - 16:45"};
    private final String[] elementary = {"8:50 - 9:30", "9:40 - 10:20", "10:40 - 11:20", "11:30 - 12:10", "13:10 - 13:50", "14:00 - 14:40", "15:10 - 15:50", "16:00 - 16:40"};

    private final SharedPreferenceUtil sharedPreferenceUtil;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference().child("timetable");
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final Gson gson = new Gson();

    public TimetableUtil(Context context) {
        this.context = context;
        sharedPreferenceUtil = new SharedPreferenceUtil(context);
    }

    public void saveTimetable(TextView[][] textViews) {
        for (int i = 0; i < 5; i++) {
            for (int x = 0; x < 8; x++) {
                try {
                    timetable[i][x][0] = textViews[i][x].getText().toString();
                    timetable[i][x][1] = "#" + Integer.toHexString(((ColorDrawable) textViews[i][x].getBackground()).getColor()).toUpperCase();
                } catch (NullPointerException e) {
                    timetable[i][x][0] = "";
                    timetable[i][x][1] = "";
                }
            }
        }
        sharedPreferenceUtil.putString("timetable_content", gson.toJson(timetable));
    }

    public void setTimetable(TextView[][] textViews, TextView[] times, ActionBar actionBar) {
        String content = sharedPreferenceUtil.getString("timetable_content", defaultContent());
        timetable = gson.fromJson(content, String[][][].class);

        if(actionBar!=null) {
            actionBar.setTitle(sharedPreferenceUtil.getString("timetable_title", "시간표"));
            actionBar.setSubtitle(sharedPreferenceUtil.getString("timetable_people", user.getDisplayName()));
        }

        int j = 0;
        for (TextView textView : times) {
            if (sharedPreferenceUtil.getString("timetable_title", "초등학교").contains("중학교")) {
                textView.setText(Html.fromHtml(j + 1 + "<br><small><small>" + middle[j] + "</small></small>"));
            } else {
                textView.setText(Html.fromHtml(j + 1 + "<br><small><small>" + elementary[j] + "</small></small>"));
            }
            j++;
        }

        for (int i = 0; i < 5; i++) {
            for (int b = 0; b < 8; b++) {
                try {
                    textViews[i][b].setText(timetable[i][b][0]);
                    textViews[i][b].setBackgroundColor(Color.parseColor(timetable[i][b][1]));
                    textViews[i][b].setTextColor(Color.parseColor(getTextColor(timetable[i][b][1])));
                    //색상이 밝으면 글자색 검정
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }
    }

    public void uploadTimetable(String school, String year, String grade, String semester) {
        String timetableTitle = school + " " + grade + " " + semester + "(" + year + ")";
        DatabaseReference position = databaseReference.child(school).child(year).child(grade).child(semester);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", timetableTitle);
        hashMap.put("content", sharedPreferenceUtil.getString("timetable_content", ""));
        hashMap.put("people", user.getDisplayName());
        hashMap.put("profile", String.valueOf(user.getPhotoUrl()));

        position.child(user.getUid()).setValue(hashMap).addOnFailureListener(e -> Toast.makeText(context, "오류가 발생해 시간표 업로드에 실패했습니다.", Toast.LENGTH_LONG).show());
    }

    public void findTimetable(String school, String year, String grade, String semester, ListView listView, TimetableListAdapter adapter) {
        DatabaseReference position = databaseReference.child(school).child(year).child(grade).child(semester);

        position.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
                    };
                    adapter.addItem(snapshot.getValue(t).get("title"), snapshot.getValue(t).get("content"), snapshot.getValue(t).get("people"), snapshot.getValue(t).get("profile"));
                }

                listView.setVisibility(View.VISIBLE);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public String defaultContent() {
        return "[[[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"]],[[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"]],[[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"]],[[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"]],[[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"]]]";
    }

    public String contentList() {
        String msg = "오늘의 시간표가 없습니다.";
        String timetableContent = sharedPreferenceUtil.getString("timetable_content", "");

        if (!timetableContent.isEmpty() && !timetableContent.equals(defaultContent())) {
            timetable = new Gson().fromJson(timetableContent, String[][][].class);
            int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;

            if (day >= 0 && day <= 4) {
                for (int i = 0; i < 8; i++) {
                    String content = timetable[day][i][0]; //0은 시간표 내용 1은 배경색
                    if (!content.isEmpty()) {
                        if (msg.equals("오늘의 시간표가 없습니다.")) {
                            msg = "";
                        }
                        msg += (i + 1) + "교시 : " + content + "\n";
                    }
                }
            } //시간표 가져오기
        } else {
            msg = "시간표를 검색 또는 생성을 해주세요!";
        }

        return msg.replaceAll("\n$", "");
    }

    public String getTextColor(String color) {
        String textColor = "#ffffff";
        if (color.equals("#FF8BC34A") || color.equals("#FFCDDC39") || color.equals("#FFFFEB3B") || color.equals("#FFFFC107") || color.equals("#FFFF9800") || color.equals("#FFFFFFFF")) {
            textColor = "#000000";
        }
        return textColor;
    }

}
