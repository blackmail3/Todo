package com.example.comp7506.todolist.Receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.comp7506.todolist.R;
import com.example.comp7506.todolist.Service.AlarmService;
import com.example.comp7506.todolist.Activity.MainActivity;
import com.example.comp7506.todolist.Utils.SPUtils;

public class AlarmReceiver extends BroadcastReceiver {
    
    private static final int NOTIFICATION_ID_1 = 0x00113;
    private String title;
    private String dsc;
    private static final String TAG = "receiver";
    private static final String KEY_RINGTONE = "ring_tone";
    private static final String KEY_VIBRATE = "vibrator";
    private String ringTone;
    private String CHANNEL_ID = "todo";
    private String CHANNEL_NAME = "Todo Notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        ringTone = intent.getStringExtra("ringTone");
        title = intent.getStringExtra("title");
        Log.i(TAG, title);
        dsc = intent.getStringExtra("dsc");
        showNormal(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, AlarmService.class);
        context.startService(intent);  //回调Service,同一个Service只会启动一个，所以直接再次启动Service，会重置开启新的提醒，
    }
    /**
     * Send Notification
     */
    private void showNormal(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.today)     //icon
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setTicker(title)        //Notification content displayed in the status bar
                    .setContentInfo("Wise Reminder")        //Content information
                    .setContentTitle(title)        //Set notification title
                    .setContentText(dsc)        //Set notification content
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setFullScreenIntent(pi, true)
                    .setContentIntent(pi);

        if (((String) SPUtils.get(context, KEY_RINGTONE, "")).equals("")){
            builder.setDefaults(Notification.DEFAULT_ALL);
            Log.i(TAG, "Default Ringtone");
        } else {
            builder.setSound(Uri.parse((String) SPUtils.get(context, KEY_RINGTONE, "")));
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            Log.i(TAG, (String) SPUtils.get(context, KEY_RINGTONE, ""));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        // Compatible with API 26，Android 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);
        }
        builder.build();
        manager.notify(NOTIFICATION_ID_1, builder.build());
    }}
