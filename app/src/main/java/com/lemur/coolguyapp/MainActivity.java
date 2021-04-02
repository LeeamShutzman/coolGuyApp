package com.lemur.coolguyapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCES = "sharedPreferences";

    TextView text;
    TextView numText;
    Button settings;
    Dialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        int numDays = sharedPreferences.getInt(initialSetup.NUM_DAYS, 0);
        int newStatus = sharedPreferences.getInt(initialSetup.NEW_STATUS, 0); //if first time opening app, newStatus won't exist and will be 0 and settings screen will open

        text = findViewById(R.id.text);
        numText = findViewById(R.id.chadDays);

        if(newStatus == 0) //open settings if first time
        {
            openSettings();
        }
        else
        {
            numText.setText(String.valueOf(numDays));
        }

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_pop_up);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.allcorners));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        lp.y = 120;

        Button noButton = dialog.findViewById(R.id.noButton);
        Button yesButton = dialog.findViewById(R.id.yesButton);

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFromNotification(numText);
                manager.cancel(200);
                dialog.dismiss();
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment(numText);
                manager.cancel(200);
                dialog.dismiss();
            }
        });

        Intent intent = getIntent();

        //from home screen section
        if(intent.getBooleanExtra(MyReceiver.REGULAR_PRESS, false) && !sharedPreferences.getBoolean(MyReceiver.UPDATED, true))
        { //checks if user opened the app from the home screen and if the counter hasn't been updated
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            }, 300);
        }
        //from notification section
        else if(!sharedPreferences.getBoolean(MyReceiver.UPDATED, true))
        { //if it hasn't been updated but the user came from the notification, MrReceiver.REGULAR_PRESS will have been set to true
            Intent yesNoIntent = new Intent(MainActivity.this, MainActivity.class);
            setIntent(yesNoIntent); //resets the intent so MainActivity doesn't think user came from notification when coming from home screen if they don't update when the come from notification

            if(intent.getBooleanExtra(MyReceiver.FROM_NOTIFICATION, false) && (sharedPreferences.getBoolean(MyReceiver.FIRST_TIME, true)))
            { //action buttons pressed (FIRST_TIME might be unnecessary, test and possibly remove)

                dialog.dismiss();
                manager.cancel(200);

                if (intent.getBooleanExtra(MyReceiver.NO, false)) //yes pressed
                {
                    increment(numText);
                }
                else //no pressed
                {
                    resetFromNotification(numText);
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean(MyReceiver.FIRST_TIME, false);
                editor.apply();

            }
            else //notification pressed
            {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                }, 300);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        //don't want to go back to initial setup screen
    }

    public void resetFromNotification(TextView passedText){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(initialSetup.NUM_DAYS, 0);
        editor.putBoolean(MyReceiver.UPDATED, true);

        editor.commit();

        passedText.setText("0");
    }


    public void increment(TextView passedText){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        int numDays = sharedPreferences.getInt(initialSetup.NUM_DAYS, 0);

        numDays++;

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(initialSetup.NUM_DAYS, numDays);
        editor.putBoolean(MyReceiver.UPDATED, true);

        editor.commit();

        passedText.setText(String.valueOf(numDays));
    }

    public void openSettings(){
        Intent settingsIntent = new Intent(this, initialSetup.class);
        startActivity(settingsIntent);
    }
}


