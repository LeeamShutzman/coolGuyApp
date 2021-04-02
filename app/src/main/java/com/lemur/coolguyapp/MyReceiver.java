package com.lemur.coolguyapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    public static final String UPDATED = "updated";
    public static final String FIRST_TIME = "firstTime"; //indicates if first notification press, potentially useless, might discard
    public static final String REGULAR_PRESS = "regularPress"; //indicates if notification pressed but not action button
    public static final String FROM_NOTIFICATION= "fromNotification"; //used to determine if action button was pressed
    public static final String NO= "no"; //action button variable, indicates which button was pressed
    public static final String NUM_KEY = "com.lemur.coolguyapp.NUM_KEY";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(UPDATED, false); //set updated variable to false when notification time elapses
        editor.putBoolean(FIRST_TIME, true); // might be useless

        editor.commit();

        //intent for pressing notification
        Intent nextIntent = new Intent(context, MainActivity.class);
        nextIntent.putExtra(REGULAR_PRESS, true);
        nextIntent.putExtra(FROM_NOTIFICATION, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextIntent, 0);

        //intent for pressing no
        Intent nIntent = new Intent(context, MainActivity.class);
        nIntent.putExtra(NO, true);
        nIntent.putExtra(FROM_NOTIFICATION, true);
        PendingIntent pYesIntent = PendingIntent.getActivity(context, 6, nIntent, 0);

        //intent for pressing yes
        Intent yIntent = new Intent(context, MainActivity.class);
        yIntent.putExtra(NO, false);
        yIntent.putExtra(FROM_NOTIFICATION, true);
        PendingIntent pNoIntent = PendingIntent.getActivity(context, 9, yIntent, 0);

        //set notification settings
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "helloThere")
                .setContentTitle("Tell me the truth,")
                .setContentText("Were you a cool guy today?")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.a99rnbdjdo7qbirto4a5ivqhr3)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.a99rnbdjdo7qbirto4a5ivqhr3, "Yes", pYesIntent)
                .addAction(R.drawable.a99rnbdjdo7qbirto4a5ivqhr3, "No", pNoIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200,builder.build());

    }
}

