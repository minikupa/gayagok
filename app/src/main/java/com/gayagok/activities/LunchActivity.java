package com.gayagok.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.gayagok.views.LoadingDialog;
import com.gayagok.views.LunchAdapter;
import com.gayagok.models.LunchItem;
import com.gayagok.R;
import com.gayagok.databinding.ActivityLunchBinding;
import com.gayagok.utils.LunchListener;
import com.gayagok.utils.LunchUtil;
import com.gayagok.utils.SharedPreferenceUtil;
import com.gayagok.views.Popup_day;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class LunchActivity extends AppCompatActivity {

    private SharedPreferenceUtil sharedPreferenceUtil;
    private LunchUtil lunchUtil;
    private int first = 0;

    private final Calendar calendar = Calendar.getInstance();

    private LinearLayoutManager linearLayoutManager;
    private ActivityLunchBinding binding;
    private LoadingDialog myDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lunch);

        binding.recyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                binding.recyclerview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                new Popup_day(LunchActivity.this);
            }
        });

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(linearLayoutManager);

        binding.refreshLayout.setOnRefreshListener(() -> parse(calendar));
        binding.refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);

        binding.back.bringToFront();
        binding.back.setOnClickListener((v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            first = 0;
            viewSetting();
        }));

        binding.forward.bringToFront();
        binding.forward.setOnClickListener((v -> {
            first = 0;
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            viewSetting();
        }));

        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        lunchUtil = new LunchUtil(this);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (!sharedPreferenceUtil.getString("lunch_" + lunchUtil.getKey(calendar), "").isEmpty()) {
            viewSetting();
        } else {
            parse(calendar);
        }
    }

    private void viewSetting() {
        first++;
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        HashMap<String, LunchItem> list = lunchUtil.getList(calendar);

        if (list != null && list.containsKey(lunchUtil.getKey(calendar))) { //이번주 급식이 들어있는지 체크
            List<LunchItem> weekDays = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                weekDays.add(list.get(lunchUtil.getKey(calendar)));
                calendar.add(Calendar.DATE, 1);
            }

            String menu = weekDays.get(0).menu;
            boolean noLunch = false;
            for (LunchItem lunchItem : weekDays) {
                noLunch = menu.equals(lunchItem.menu);
            }
            LunchAdapter listAdapter = new LunchAdapter(linearLayoutManager, weekDays);
            binding.recyclerview.setAdapter(listAdapter);

            Calendar temp = Calendar.getInstance();
            int day = temp.get(Calendar.DAY_OF_WEEK);
            if (day != 1 && day != 7 && calendar.get(Calendar.WEEK_OF_MONTH) == temp.get(Calendar.WEEK_OF_MONTH)) {
                binding.recyclerview.scrollToPosition(day - 2);
            } else {
                binding.recyclerview.scrollToPosition(0);
            }

            if (noLunch && first == 1) {
                parse(calendar);
            }
        } else {
            parse(calendar);
        }
    }

    private void parse(Calendar calendar) {
        if (!LunchActivity.this.isFinishing()) {
            myDialog = new LoadingDialog(LunchActivity.this);
            myDialog.show();
        }
        lunchUtil.parseLunch(new LunchListener() {

            @Override
            public void onLoadSuccess() {
                runOnUiThread(() -> {
                    myDialog.dismiss();
                    binding.refreshLayout.setRefreshing(false);
                    first = 1;
                    viewSetting();
                });
            }

            @Override
            public void onLoadError() {
                runOnUiThread(() -> {
                    myDialog.dismiss();
                    binding.refreshLayout.setRefreshing(false);
                    Toast.makeText(LunchActivity.this, "인터넷의 문제나, 나이스에 오류가 있습니다.", Toast.LENGTH_SHORT).show();
                });
            }
        }, calendar);

    }

}

