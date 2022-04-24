package com.example.comp7506.todolist.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import cn.bmob.v3.Bmob;

public class NetworkReceiver extends BroadcastReceiver {

    private static final String APP_ID = "1c54d5b204e98654778c77547afc7a66";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if ( info != null && info.isAvailable()){
            Bmob.resetDomain("https://open3.bmob.cn/8/");
            Bmob.initialize(context, APP_ID);
            Log.i("Network status", "Network connection ");
        }else {
            Log.i("Network status", "No network connection");
        }
    }
}
