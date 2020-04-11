package com.gayagok.activities;

import android.content.DialogInterface;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.gayagok.R;
import com.gayagok.databinding.DialogFindshareBinding;
import com.gayagok.databinding.FragmentTimetable1Binding;
import com.gayagok.models.TimetableItem;
import com.gayagok.utils.SharedPreferenceUtil;
import com.gayagok.utils.TimetableUtil;
import com.gayagok.views.TimetableListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class TimetableFragment1 extends Fragment {

    private TextView[][] days;
    private TextView[] times;

    private FragmentTimetable1Binding binding;

    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private SharedPreferenceUtil sharedPreferenceUtil;
    private TimetableUtil timetableUtil;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timetable1, container, false);
        View view = binding.getRoot();
        binding.setFragment(this);

        sharedPreferenceUtil = new SharedPreferenceUtil(getActivity());
        timetableUtil = new TimetableUtil(getActivity());

        days = new TextView[][]{{binding.monday1, binding.monday2, binding.monday3, binding.monday4, binding.monday5, binding.monday6, binding.monday7, binding.monday8},
                {binding.tuesday1, binding.tuesday2, binding.tuesday3, binding.tuesday4, binding.tuesday5, binding.tuesday6, binding.tuesday7, binding.tuesday8},
                {binding.wednesday1, binding.wednesday2, binding.wednesday3, binding.wednesday4, binding.wednesday5, binding.wednesday6, binding.wednesday7, binding.wednesday8},
                {binding.thursday1, binding.thursday2, binding.thursday3, binding.thursday4, binding.thursday5, binding.thursday6, binding.thursday7, binding.thursday8},
                {binding.friday1, binding.friday2, binding.friday3, binding.friday4, binding.friday5, binding.friday6, binding.friday7, binding.friday8}};
        times = new TextView[]{binding.time1, binding.time2, binding.time3, binding.time4, binding.time5, binding.time6, binding.time7, binding.time8};

        setTextview();

        if (!sharedPreferenceUtil.getString("timetable_content", "").isEmpty()) {
            timetableUtil.setTimetable(days, times, null);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        timetableUtil.saveTimetable(days); //화면 가려졌을때 content 저장
    }

    private void setTextview(){
        int dp = Math.min(getActivity().getResources().getDisplayMetrics().widthPixels / 520, getActivity().getResources().getDisplayMetrics().heightPixels / 520);
        for (TextView[] i : days) {
            for (TextView x : i) {
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(getActivity().getResources().getDisplayMetrics().widthPixels / 6, getActivity().getResources().getDisplayMetrics().heightPixels / 11);
                layoutParams.setMargins(dp, dp, dp, dp);

                x.setLayoutParams(layoutParams);
                x.setOnClickListener(v -> makeDialog(x).show());
            }
        }
        for (TextView textView : times) {
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(getActivity().getResources().getDisplayMetrics().widthPixels / 8, getActivity().getResources().getDisplayMetrics().heightPixels / 11);
            layoutParams.setMargins(dp, dp, dp, dp);
            textView.setLayoutParams(layoutParams);
        }
    }

    public void makeButtonPress(View view) {
        binding.floatingMenu.collapse();
        sharedPreferenceUtil.putString("timetable_content", timetableUtil.defaultContent())
                .putString("timetable_title", "시간표")
                .putString("timetable_people", user.getDisplayName());
        timetableUtil.setTimetable(days, times, ((AppCompatActivity) getActivity()).getSupportActionBar());
    }

    public void findButtonPress(View view) {
        binding.floatingMenu.collapse();
        findShareDialog("시간표 검색");
    }

    public void shareButtonPress(View view) {
        findShareDialog("시간표를 공유하시겠습니까?");
    }

    private AlertDialog makeDialog(TextView textView) {
        View innerView = getLayoutInflater().inflate(R.layout.dialog_make, null);
        String[] colorArr = {"#" + Integer.toHexString(((ColorDrawable) textView.getBackground()).getColor()).toUpperCase()};

        binding.floatingMenu.collapse();
        EditText editText = innerView.findViewById(R.id.subjectName);
        Button button = innerView.findViewById(R.id.color);

        editText.setText(textView.getText());
        button.setBackgroundColor(((ColorDrawable) textView.getBackground()).getColor());


        AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
        ab.setTitle("시간표 생성");
        ab.setView(innerView);

        button.setOnClickListener((View view) ->
                new SpectrumDialog.Builder(getActivity())
                        .setTitle("색상 선택")
                        .setColors(R.array.material_colors)
                        .setSelectedColor(((ColorDrawable) textView.getBackground()).getColor())
                        .setDismissOnColorSelected(false)
                        .setOnColorSelectedListener((boolean positiveResult, @ColorInt int color) -> {
                                    if (positiveResult) {
                                        colorArr[0] = ("#" + Integer.toHexString(color).toUpperCase());
                                        button.setBackgroundColor(Color.parseColor(colorArr[0]));
                                    }
                                }
                        ).build().show(getActivity().getSupportFragmentManager(), "dialog")

        ); //버튼눌러 색상선택 다이얼로그 띄우고 결과 배열에저장

        ab.setPositiveButton("확인", (DialogInterface arg0, int arg1) -> {
            textView.setBackgroundColor(Color.parseColor(colorArr[0]));
            textView.setText(editText.getText().toString());

            textView.setTextColor(Color.parseColor(timetableUtil.getTextColor(colorArr[0]))); //색상이 밝으면 글자색 검정

        });    // 확인버튼누르면 그 색이랑 이름을 시간표칸에다 적용
        ab.setNegativeButton("삭제", (
                DialogInterface arg0, int arg1) ->

        {
            textView.setBackgroundColor(Color.parseColor("#ffffff"));
            textView.setText("");
        }); //취소버튼이름을 삭제로 변경, 누르면 휜색 and 글자무

        return ab.create();
    }

    private void findShareDialog(String title) {
        View innerView = getLayoutInflater().inflate(R.layout.dialog_findshare, null);
        DialogFindshareBinding binding2 = DataBindingUtil.bind(innerView);
        AlertDialog ab = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(innerView)
                .setPositiveButton("확인", null)
                .create();

        timetableUtil.saveTimetable(days); //content 저장

        ArrayAdapter elementaryAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.share_elementary_grade, android.R.layout.simple_spinner_item);
        ArrayAdapter middleAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.share_middle_grade, android.R.layout.simple_spinner_item);

        elementaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        middleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 2019; i <= calendar.get(Calendar.YEAR); i++) {
            list.add(i + "년");
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                list
        );
        binding2.year.setAdapter(yearAdapter);

        binding2.school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int i = (int) binding2.grade.getSelectedItemId();
                if (position == 0) {
                    binding2.grade.setAdapter(elementaryAdapter); //학년을 6학년까지 나오게
                    binding2.grade.setSelection(i);
                } else {
                    binding2.grade.setAdapter(middleAdapter); //학년을 3학년까지 나오게
                    if (i < 3) {
                        binding2.grade.setSelection(i);
                    } else {
                        binding2.grade.setSelection(2);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ab.setButton(AlertDialog.BUTTON_NEGATIVE, "취소", (DialogInterface.OnClickListener) null);
        ab.setOnShowListener((DialogInterface dialog) -> //확인버튼 눌러도 다이얼로그 안나가짐
                ab.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((View view) -> {

                    String schoolString = binding2.school.getSelectedItem().toString();
                    String gradeString = binding2.grade.getSelectedItem().toString();
                    String semesterString = binding2.semester.getSelectedItem().toString();
                    String yearString = binding2.year.getSelectedItem().toString();


                    if (title.equals("시간표를 공유하시겠습니까?")) {

                        String timetableTitle = schoolString + " " + gradeString + " " + semesterString + "(" + yearString + ")";
                        timetableUtil.uploadTimetable(schoolString, yearString, gradeString, semesterString);

                        sharedPreferenceUtil.putString("timetable_title", timetableTitle).putString("timetable_people", user.getDisplayName());
                        timetableUtil.setTimetable(days, times, ((AppCompatActivity) getActivity()).getSupportActionBar());
                        binding.floatingMenu.collapse();
                        dialog.dismiss();
                    } else {
                        TimetableListAdapter adapter = new TimetableListAdapter();
                        timetableUtil.findTimetable(schoolString, yearString, gradeString, semesterString, binding2.listView, adapter);

                        binding2.listView.setOnItemClickListener((AdapterView<?> parent, View viewX, int positionX, long id) -> {
                            TimetableItem item = adapter.getItem(positionX);
                            sharedPreferenceUtil.putString("timetable_title", item.title).putString("timetable_content", item.content).putString("timetable_people", item.people);//선택 시간표를 저장
                            timetableUtil.setTimetable(days, times, ((AppCompatActivity) getActivity()).getSupportActionBar());
                            binding.floatingMenu.collapse();
                            dialog.dismiss();
                        });

                    }
                }));
        ab.show();

    }

}
