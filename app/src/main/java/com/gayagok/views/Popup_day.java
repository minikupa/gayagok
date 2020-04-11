package com.gayagok.views;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gayagok.R;

import java.util.Calendar;

public class Popup_day {

    private static int count = 0;

    public Popup_day(Context context) {
        Calendar calendar = Calendar.getInstance();
        int minute = calendar.get(Calendar.MINUTE);

        if(count == 0) {
            if (calendar.get(Calendar.HOUR) == 5 && minute > 10 && minute < 19) {
                new Handler().postDelayed(() -> popUp(context), 60000 * (18 - minute) + 1000 * (60 - calendar.get(Calendar.SECOND)));
            } else if (calendar.get(Calendar.HOUR) == 5 && minute == 19) {
                popUp(context);
            }
        }

    }

    private void popUp(Context context){
        count++;
        View popupView = ((AppCompatActivity) context).getLayoutInflater().inflate(R.layout.popup_day, null);
        PopupWindow mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView textView = mPopupWindow.getContentView().findViewById(R.id.textView);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.day);
        textView.startAnimation(anim);

        mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        new Handler().postDelayed(() -> mPopupWindow.dismiss(), 5000);
    }
}


