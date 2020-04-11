package com.gayagok.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gayagok.R;
import com.gayagok.activities.TimetableFragment1;
import com.gayagok.activities.TimetableFragment2;
import com.gayagok.activities.TimetableFragment3;

public class TimetablePagerAdapter extends FragmentPagerAdapter {

    private final int mNumOfTabs;
    private final Context context;
    private final int[] imageResId = {
            R.drawable.ic_day,
            R.drawable.ic_night,
            R.drawable.ic_exam};
    private final String[] tabTitles = {
            "시간표",
            "야자/방과후",
            "시험"};

    public TimetablePagerAdapter(FragmentManager fm, int NumOfTabs, Context context) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int i) {

        Drawable image = context.getResources().getDrawable(imageResId[i]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());

        SpannableString sb = new SpannableString("   " + tabTitles[i]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new TimetableFragment1();
            case 1:
                return new TimetableFragment2();
            case 2:
                return new TimetableFragment3();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
