package com.lemur.coolguyapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.RequiresApi;

import static android.content.Context.ALARM_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

//set alarm on boot
public class BootReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, sharedPreferences.getInt("alarmTime", 8));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if(Calendar.getInstance().getTimeInMillis() > calendar.getTimeInMillis())
            calendar.add(Calendar.DATE, 1);

        Intent notifyIntent = new Intent(context,MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 69, notifyIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void createNotificationChannel(Context context){
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LemurReminderChannel";
            String description = "Channel for Lemur Reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel("helloThere", name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}



