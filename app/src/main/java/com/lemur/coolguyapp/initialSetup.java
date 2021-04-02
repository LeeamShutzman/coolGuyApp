package com.lemur.coolguyapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class initialSetup extends AppCompatActivity {
    public static final String NUM_DAYS = "numDays";
    public static final String NEW_STATUS = "newStatus";
    public static final String ALARM_TIME = "alarmTime";

    SharedPreferences sharedPreferences;

    private Button button;
    private NumberPicker dayPicker;
    private NumberPicker alarmPicker;
    private SwitchCompat amPmSwitch;

    private int days;
    private int time;
    private boolean pm = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);

        //for coming from main screen, and setting default
        days = sharedPreferences.getInt(NUM_DAYS, 0);
        time = sharedPreferences.getInt(ALARM_TIME, 20);

        //convert 24 hour time to 12 hour, and set am/pm switch
        if((time > 12) && (time != 24)){
            time -= 12;
            pm = true;
        }
        else if(time == 24){
            time-=12;
        }
        else if(time == 12){
            pm = true;
        }

        dayPicker = findViewById(R.id.daysPicker);
        alarmPicker = findViewById(R.id.timePicker);
        amPmSwitch = findViewById(R.id.amPmPicker);

        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(365);
        dayPicker.setValue(days);

        alarmPicker.setMinValue(1);
        alarmPicker.setMaxValue(12);
        alarmPicker.setValue(time);

        amPmSwitch.setChecked(pm);

        button = findViewById(R.id.button);

        //set alarm and upload days and alarm time to sharedPreferences
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hr = alarmPicker.getValue();

                if(((amPmSwitch.isChecked()) && (hr != 12)) || (!amPmSwitch.isChecked()) && (hr == 12)) {
                    hr += 12;
                }

                setAlarm(hr);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt(NUM_DAYS, dayPicker.getValue());
                editor.putInt(NEW_STATUS, 1);
                editor.putInt(ALARM_TIME, hr);

                editor.commit();

                Intent intent = new Intent(initialSetup.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES, MODE_PRIVATE);
        if(sharedPreferences.getInt(NEW_STATUS, 0)==1) {
            super.onBackPressed();
        }
    }

    private void createNotificationChannel(){
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LemurReminderChannel";
            String description = "Channel for Lemur Reminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel("helloThere", name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAlarm(int hr){
        createNotificationChannel();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hr);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if(Calendar.getInstance().getTimeInMillis() > calendar.getTimeInMillis())
            calendar.add(Calendar.DATE, 1);

        Intent notifyIntent = new Intent(getApplicationContext(),MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 69, notifyIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
