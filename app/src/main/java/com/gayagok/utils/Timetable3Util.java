package com.gayagok.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
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

import java.util.HashMap;

public class Timetable3Util {

    private final Context context;

    private String[][][] exam = new String[2][4][2];
    private final String[] middle = {"8:50 - 9:35", "9:45 - 10:30", "10:40 - 11:25", "11:35 - 12:20"};

    private final SharedPreferenceUtil sharedPreferenceUtil;
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = firebaseDatabase.getReference().child("exam");
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final Gson gson = new Gson();

    public Timetable3Util(Context context) {
        this.context = context;
        sharedPreferenceUtil = new SharedPreferenceUtil(context);
    }

    public void saveTimetable(TextView[][] textViews) {
        for (int i = 0; i < 2; i++) {
            for (int x = 0; x < 4; x++) {
                try {
                    exam[i][x][0] = textViews[i][x].getText().toString();
                    exam[i][x][1] = "#" + Integer.toHexString(((ColorDrawable) textViews[i][x].getBackground()).getColor()).toUpperCase();
                } catch (NullPointerException e) {
                    exam[i][x][0] = "";
                    exam[i][x][1] = "";
                }
            }
        }
        sharedPreferenceUtil.putString("exam_content", gson.toJson(exam));
    }

    public void setTimetable(TextView[][] textViews, TextView[] times, ActionBar actionBar) {
        String content = sharedPreferenceUtil.getString("exam_content", defaultContent());
        exam = gson.fromJson(content, String[][][].class);

        if (actionBar != null) {
            actionBar.setTitle(sharedPreferenceUtil.getString("exam_title", "시간표"));
            actionBar.setSubtitle(sharedPreferenceUtil.getString("exam_people", user.getDisplayName()));
        }

        int j = 0;
        for (TextView textView : times) {
            textView.setText(Html.fromHtml(j + 1 + "<br><small><small>" + middle[j] + "</small></small>"));
            j++;
        }

        for (int i = 0; i < 2; i++) {
            for (int b = 0; b < 4; b++) {
                try {
                    textViews[i][b].setText(exam[i][b][0]);
                    textViews[i][b].setBackgroundColor(Color.parseColor(exam[i][b][1]));
                    textViews[i][b].setTextColor(Color.parseColor(getTextColor(exam[i][b][1])));
                    //색상이 밝으면 글자색 검정
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        }
    }

    public void uploadTimetable(String year, String grade, String semester, String exam) {
        String timetableTitle = grade + " " + semester + " " + exam + "(" + year + ")";
        DatabaseReference position = databaseReference.child(year).child(grade).child(semester).child(exam);

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", timetableTitle);
        hashMap.put("content", sharedPreferenceUtil.getString("exam_content", ""));
        hashMap.put("people", user.getDisplayName());
        hashMap.put("profile", String.valueOf(user.getPhotoUrl()));

        position.child(user.getUid()).setValue(hashMap).addOnFailureListener(e -> Toast.makeText(context, "오류가 발생해 시간표 업로드에 실패했습니다.", Toast.LENGTH_LONG).show());
    }

    public void findTimetable(String year, String grade, String semester, String exam, ListView listView, TimetableListAdapter adapter) {
        DatabaseReference position = databaseReference.child(year).child(grade).child(semester).child(exam);

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
        return "[[[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"]],[[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"],[\"\",\"#FFFFFFFF\"]]]";
    }

    public String getTextColor(String color) {
        String textColor = "#ffffff";
        if (color.equals("#FF8BC34A") || color.equals("#FFCDDC39") || color.equals("#FFFFEB3B") || color.equals("#FFFFC107") || color.equals("#FFFF9800") || color.equals("#FFFFFFFF")) {
            textColor = "#000000";
        }
        return textColor;
    }

}
