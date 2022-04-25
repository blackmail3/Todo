package com.example.comp7506_1.todolist.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.comp7506_1.todolist.Activity.MainActivity;
import com.example.comp7506_1.todolist.Service.AlarmService;

/**
 * 自启动
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.AUDIO_BECOMING_NOISY")) {
            /* 服务开机自启动 */
            Intent service = new Intent(context, AlarmService.class);
            context.startService(service);
            Log.i("BootUp", "自启动成功");
        }

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* 应用开机自启动 */
            Intent intent_n = new Intent(context, MainActivity.class);

            intent_n.setAction("android.intent.action.MAIN");
            intent_n.addCategory("android.intent.category.LAUNCHER");
            intent_n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent_n);
            Log.i("BootUp", "自启动成功");
        }


    }
}
