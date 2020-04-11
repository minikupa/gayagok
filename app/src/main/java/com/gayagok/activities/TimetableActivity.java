package com.gayagok.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.gayagok.R;
import com.gayagok.databinding.ActivityTimetableBinding;
import com.gayagok.utils.SharedPreferenceUtil;
import com.gayagok.views.Popup_day;
import com.gayagok.views.TimetablePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class TimetableActivity extends AppCompatActivity {

    private ActivityTimetableBinding binding;
    private SharedPreferenceUtil sharedPreferenceUtil;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timetable);

        setSupportActionBar(binding.toolbar);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);

        getSupportActionBar().setTitle(sharedPreferenceUtil.getString("timetable_title", "시간표"));

        if (user != null) {
            getSupportActionBar().setSubtitle(sharedPreferenceUtil.getString("timetable_people", user.getDisplayName()));

            TimetablePagerAdapter adapter = new TimetablePagerAdapter(getSupportFragmentManager(), 3, getApplicationContext());
            binding.viewPager.setAdapter(adapter);
            binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            getSupportActionBar().setTitle(sharedPreferenceUtil.getString("timetable_title", "시간표"));
                            getSupportActionBar().setSubtitle(sharedPreferenceUtil.getString("timetable_people", user.getDisplayName()));
                            break;
                        case 1:
                            getSupportActionBar().setTitle(sharedPreferenceUtil.getString("image_title", "시간표"));
                            getSupportActionBar().setSubtitle(sharedPreferenceUtil.getString("image_people", user.getDisplayName()));
                            break;
                        case 2:
                            getSupportActionBar().setTitle(sharedPreferenceUtil.getString("exam_title", "시간표"));
                            getSupportActionBar().setSubtitle(sharedPreferenceUtil.getString("exam_people", user.getDisplayName()));
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            binding.tabLayout.setupWithViewPager(binding.viewPager);

        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setLogo(R.drawable.ic_logo)
                            .setAvailableProviders(providers)
                            .build(),
                    100);

            Toast.makeText(this, "로그인을 해야 사용할 수 있는 서비스입니다.", Toast.LENGTH_SHORT).show();
        }

        binding.viewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                binding.viewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                new Popup_day(TimetableActivity.this);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                getSupportActionBar().setSubtitle(user.getDisplayName());

                TimetablePagerAdapter adapter = new TimetablePagerAdapter(getSupportFragmentManager(), 3, getApplicationContext());
                binding.viewPager.setAdapter(adapter);
                binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        switch (position) {
                            case 0:
                                getSupportActionBar().setTitle(sharedPreferenceUtil.getString("timetable_title", "시간표"));
                                getSupportActionBar().setSubtitle(sharedPreferenceUtil.getString("timetable_people", user.getDisplayName()));
                                break;
                            case 1:
                                getSupportActionBar().setTitle(sharedPreferenceUtil.getString("image_title", "시간표"));
                                getSupportActionBar().setSubtitle(sharedPreferenceUtil.getString("image_people", user.getDisplayName()));
                                break;
                            case 2:
                                getSupportActionBar().setTitle(sharedPreferenceUtil.getString("exam_title", "시간표"));
                                getSupportActionBar().setSubtitle(sharedPreferenceUtil.getString("exam_people", user.getDisplayName()));
                                break;
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                binding.tabLayout.setupWithViewPager(binding.viewPager);
            } else {
                finish();
                Toast.makeText(this, "알 수 없는 오류가 생겼습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
