package com.gayagok.activities;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.gayagok.R;
import com.gayagok.databinding.ActivityScheduleBinding;
import com.gayagok.utils.ScheduleListener;
import com.gayagok.utils.ScheduleUtil;
import com.gayagok.utils.SharedPreferenceUtil;
import com.gayagok.views.EventDecorator;
import com.gayagok.views.Popup_day;
import com.gayagok.views.SaturdayDecorator;
import com.gayagok.views.ScheduleListAdapter;
import com.gayagok.views.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private ActivityScheduleBinding binding;

    private ScheduleUtil scheduleUtil;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private final Calendar calendar = Calendar.getInstance();
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();

    private ArrayList<CalendarDay> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule);

        scheduleUtil = new ScheduleUtil(this);
        sharedPreferenceUtil = new SharedPreferenceUtil(this, "schedule");
        numberFormat.setMinimumIntegerDigits(2);

        binding.calendarView.state()
                .edit()
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit(); //월 단위로 표시

        binding.calendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator()); //토요일과 일요일을 색 넣어서 표시

        binding.calendarView.setOnMonthChangedListener((MaterialCalendarView materialCalendarView, CalendarDay calendarDay) -> {
            calendar.set(calendarDay.getYear(), calendarDay.getMonth() - 1, calendarDay.getDay());
            setEvent();
        });

        binding.calendarView.setOnDateChangedListener((@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) -> {
            if (dates != null) {
                binding.list.setSelection(dates.indexOf(calendarDay));
                materialCalendarView.clearSelection();
            }
        });

        if (sharedPreferenceUtil.getString("mode", "").equals("mid")) {
            getSupportActionBar().setTitle("가야곡중학교");
            setEvent();
        } else if (sharedPreferenceUtil.getString("mode", "").equals("ele")) {
            getSupportActionBar().setTitle("가야곡초등학교");
            setEvent();
        } else {
            showDialog();
        }

        binding.calendarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                binding.calendarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                new Popup_day(ScheduleActivity.this);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                scheduleUtil.clearSchedule(calendar);
                setEvent();
                return true;
            case R.id.action_school:
                showDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setEvent() {
        ArrayList<String> list = scheduleUtil.getList(calendar);
        ScheduleListAdapter adapter = new ScheduleListAdapter();

        binding.calendarView.removeDecorators();

        if (list != null && !list.get(0).isEmpty() ) {
            dates = scheduleUtil.getDateList(calendar);
            binding.calendarView.addDecorator(new EventDecorator(Color.RED, dates));
            setList(binding.list, adapter, list);
        } else {
            scheduleUtil.parseSchedule(calendar, new ScheduleListener() {
                @Override
                public void onLoadSuccess(ArrayList contents, ArrayList dates) {
                    runOnUiThread(() -> {
                        ScheduleActivity.this.dates = dates;
                        binding.calendarView.addDecorator(new EventDecorator(Color.RED, dates));
                        setList(binding.list, adapter, contents);
                    });
                }

                @Override
                public void onLoadError() {
                    runOnUiThread(() -> Toast.makeText(ScheduleActivity.this, "인터넷에 문제가 있습니다.", Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    private void setList(ListView listView, ScheduleListAdapter adapter, ArrayList<String> lists){
        for(String list : lists){
            String[] data = list.split(" : ");
            adapter.addItem(data[1], calendar.get(Calendar.MONTH)+1+"월 "+data[0]+"일");
        }
        listView.setAdapter(adapter);
    }

    private void showDialog() {
        String[] items = {"초등학교", "중학교"};
        final boolean[] first = {true};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("학교를 골라주십시오.");
        builder.setItems(items, (DialogInterface dialog, int pos) -> {
            if (items[pos].equals("초등학교")) {
                sharedPreferenceUtil.putString("mode", "ele");
                getSupportActionBar().setTitle("가야곡초등학교");
            } else {
                sharedPreferenceUtil.putString("mode", "mid");
                getSupportActionBar().setTitle("가야곡중학교");
            }
            first[0] = false;
            setEvent();
        });

        builder.setOnDismissListener(dialog -> {
            if(first[0]){
                finish();
            }
        });
        builder.show();
    }


}
