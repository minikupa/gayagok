package com.gayagok.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.gayagok.models.MenusItem;
import com.gayagok.R;
import com.gayagok.databinding.ActivityMainBinding;
import com.gayagok.utils.LunchUtil;
import com.gayagok.views.Popup_day;
import com.gayagok.utils.ScheduleUtil;
import com.gayagok.utils.TimetableUtil;
import com.gayagok.views.MenusAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime = 0L;

    private ActivityMainBinding binding;

    private LunchUtil lunchUtil;
    private TimetableUtil timetableUtil;
    private ScheduleUtil scheduleUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        lunchUtil = new LunchUtil(getApplicationContext());
        timetableUtil = new TimetableUtil(getApplicationContext());
        scheduleUtil = new ScheduleUtil(getApplicationContext());

        binding.recycle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                binding.recycle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                new Popup_day(MainActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Intent it;

        switch (id) {
            case R.id.menu_about:
                it = new Intent(MainActivity.this, AboutActivity.class);
                break;
            default:
                it = new Intent(Intent.ACTION_VIEW, Uri.parse("https://namu.wiki/w/%EA%B0%80%EC%95%BC%EA%B3%A1%EC%A4%91%ED%95%99%EA%B5%90"));
                break;
        }
        startActivity(it);


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        setMenus();
    }

    @Override
    public void onBackPressed() { //뒤로가기 두번 종료
        if (backPressedTime + 2500 >= System.currentTimeMillis()) {
            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\'버튼을  한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setMenus() { //메뉴 설정
        Calendar calendar = Calendar.getInstance();
        List<MenusItem> items = new ArrayList<>();
        MenusItem[] menusItem = new MenusItem[4];

        menusItem[0] = new MenusItem(R.drawable.ic_lunch, "급식", lunchUtil.getStringMenu(calendar));
        menusItem[1] = new MenusItem(R.drawable.ic_timetable, "시간표", timetableUtil.contentList());

        ArrayList<String> scheduleList = scheduleUtil.getList(Calendar.getInstance());
        String schedule = "";
        if (scheduleList != null && !scheduleList.get(0).isEmpty()) {
            for (String i : scheduleList) {
                if (!i.contains("방학식") && i.contains("방학")) {
                    continue;
                }
                schedule += i + "\n";
            }
            menusItem[2] = new MenusItem(R.drawable.ic_schedule, "일정", schedule.replaceAll("\n$", ""));
        } else if (scheduleList == null) {
            menusItem[2] = new MenusItem(R.drawable.ic_schedule, "일정", "일정을 다운로드 해주세요!");
        } else {
            menusItem[2] = new MenusItem(R.drawable.ic_schedule, "일정", "이번달의 일정이 없습니다.");
        }
        menusItem[3] = new MenusItem(R.drawable.ic_quiz, "퀴즈", "곧 완성됩니다!");

        items.addAll(Arrays.asList(menusItem).subList(0, 4));

        binding.recycle.setAdapter(new MenusAdapter(this, items));
    }

}
