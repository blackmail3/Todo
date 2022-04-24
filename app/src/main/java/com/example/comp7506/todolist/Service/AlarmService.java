package com.example.comp7506.todolist.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.carmelo.library.KeepliveService;
import com.example.comp7506.todolist.Receiver.AlarmReceiver;
import com.example.comp7506.todolist.Utils.SPUtils;
import com.example.comp7506.todolist.Utils.ToDoUtils;
import com.example.comp7506.todolist.Bean.Todos;

import java.util.Calendar;
import java.util.List;


public class AlarmService extends KeepliveService {
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Intent startNotification;
    private static final String TAG = "service";
    private static final String KEY_RINGTONE = "ring_tone";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "service start！");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTimeInMillis(System.currentTimeMillis());

        List<Todos> todosList = ToDoUtils.getTodayTodos(this);
        if (todosList != null) {
            try {
                for (Todos todos : todosList) {
                    if (todos.getRemindTime() - System.currentTimeMillis() > 0 ) {

                        startNotification = new Intent(AlarmService.this, AlarmReceiver.class);
                        startNotification.putExtra("title", todos.getTitle());
                        startNotification.putExtra("dsc", todos.getDesc());
                        startNotification.putExtra("ringTone", (String) SPUtils.get(getApplication(), KEY_RINGTONE, ""));

                        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        pendingIntent = PendingIntent.getBroadcast(this, todos.getId(), startNotification, PendingIntent.FLAG_UPDATE_CURRENT);
                        if (todos.getIsRepeat() == 0){
                            alarmManager.set(AlarmManager.RTC_WAKEUP, todos.getRemindTime(), pendingIntent);

                            Log.i(TAG, "Send a single reminder ");
                            Log.i(TAG, "Title:" + todos.getTitle());
                            Log.i(TAG, "Time:" + todos.getRemindTime());
                            Log.i(TAG, "Date:" + System.currentTimeMillis() / 1000 / 60 / 60 / 24);
                            Log.i(TAG, "Ringtone：" + (String) SPUtils.get(getApplication(), KEY_RINGTONE, ""));
                        }else if (todos.getIsRepeat() == 1){
                            //24h
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, todos.getRemindTimeNoDay(), 1000 * 60 * 60 * 24, pendingIntent);
                            Log.i(TAG, "Send repeat reminders \n");
                            Log.i(TAG, "Title:" + todos.getTitle());
                            Log.i(TAG, "Time:" + todos.getRemindTimeNoDay());
                            Log.i(TAG, "Date:" + System.currentTimeMillis() / 1000 / 60 / 60 / 24);
                        }

                        ToDoUtils.setHasAlerted(this,todos.getId());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        return START_REDELIVER_INTENT;
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}


