package com.gayagok;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.gayagok.R;
import com.gayagok.utils.LunchUtil;
import com.gayagok.utils.SharedPreferenceUtil;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Random;

public class BootReceiver extends BroadcastReceiver {

    private final Calendar calendar = Calendar.getInstance();
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(context);
            LunchUtil lunchUtil = new LunchUtil(context);
            numberFormat.setMinimumIntegerDigits(2);
            int day = calendar.get(Calendar.DATE);
            String key = numberFormat.format(calendar.get(Calendar.MONTH) + 1) + numberFormat.format(day);

            String[] menus = lunchUtil.getStringMenu(calendar).split("\n");

            if ((menus.length != 1) && (!key.equals(sharedPreferenceUtil.getString("todayNoti", "")))) { //오늘중 급식노티를 보내지 않았고 급식정보가 없지 않을 때

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder notificationBuilder;
                if (Build.VERSION.SDK_INT >= 26) {
                    NotificationChannel mChannel = new NotificationChannel("gayagokSchool_lunch", "급식알림받기", NotificationManager.IMPORTANCE_DEFAULT);
                    mChannel.setDescription("오늘 급식에 대한 알림을 받음.");
                    notificationManager.createNotificationChannel(mChannel);
                    notificationBuilder = new Notification.Builder(context, mChannel.getId());
                } else {
                    notificationBuilder = new Notification.Builder(context);
                }

                Notification.Builder builder;
                builder = notificationBuilder.setSmallIcon(R.drawable.ic_lunch);

                builder.setContentTitle("오늘의 급식메뉴")
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_lunch))
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setContentText(menus[new Random().nextInt(menus.length)])
                        .setAutoCancel(true);
                Notification.InboxStyle inboxStyle = new Notification.InboxStyle(notificationBuilder)
                        .setSummaryText("더 보기");

                for (String menu : menus) {
                    inboxStyle.addLine(menu);
                }

                notificationBuilder.setStyle(inboxStyle);
                notificationManager.notify(0, notificationBuilder.build());
                sharedPreferenceUtil.putString("todayNoti", key);
            }
        }

    }

}