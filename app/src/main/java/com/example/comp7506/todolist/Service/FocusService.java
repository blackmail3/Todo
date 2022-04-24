package com.example.comp7506.todolist.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.comp7506.todolist.Activity.ClockActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FocusService extends Service {

    boolean flag = true;
    private ArrayList<String> packageList = new ArrayList<>();

    private Timer timer;
    private TimerTask task = new TimerTask() {

        @Override
        public void run() {

            if(isPrevent()){
                Intent intentNewActivity = new Intent(FocusService.this, ClockActivity.class);
                intentNewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentNewActivity);
            }

        }


    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getInstallApp();
        if (flag == true) {
            timer = new Timer();
            timer.schedule(task, 0, 100);
            flag = false;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void getInstallApp(){
        List<PackageInfo> packages = getApplicationContext().getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo pi = packages.get(i);
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // If it is a non-system program, add it to the list
                packageList.add(pi.packageName);
                Log.i("FocusService", pi.packageName);

            }
        }
    }

    /**
     * Gets the name of the running table bread (note: this method returns Null if there are multiple desktops and no    default desktop is specified, which needs to be handled when used)
    */
    public String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }

    public boolean isPrevent(){

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        ActivityManager mActivityManager;
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        String packageName;
        String topPackageName;
        if (Build.VERSION.SDK_INT > 20) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                    .getSystemService(Context.USAGE_STATS_SERVICE);
            long endTime = System.currentTimeMillis();
            long startTime = endTime - 10000;

            UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
            UsageEvents.Event event = new UsageEvents.Event();

            while (usageEvents.hasNextEvent()){
                // Get the next event into the event, first get the next event, if directly called at this time, the event package is null, type is 0.
                usageEvents.getNextEvent(event);
                // If this is an event that brings the application to the foreground
                if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND){
                    //packageName.
                    topPackageName = event.getPackageName();
                    for (int i = 0; i < packageList.size(); i++) {
                        if(topPackageName.equals(packageList.get(i))
                                && !topPackageName.equals(getPackageName()) ){
                            Log.i("FocusService", "阻止 "+ topPackageName);
                            return true;
                        }
                    }
                }
            }


        } else{
            // before 5.0
            // Get the running task stack (one application occupies one task stack) the most recently used task stack is first
            // 1 indicates the maximum capacity set to the collection List&lt; RunningTaskInfo&gt; infos = am.getRunningTasks(1);
            // Get the package name of the top Activity(the Activity the user is currently operating on) in the most recently run task stack
            packageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
            //Log.i(TAG,packageName);
        }
        /*
        ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
        String packageName = topActivity.getPackageName();
        */
//        Context context = getApplicationContext();
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        for (int i = 0; i < packageList.size(); i++) {
//            if(packageName.equals(packageList.get(i)) && !packageName.equals("com.example.lulin.todolist") ){
//                Log.i("FocusService", "阻止"+ packageName);
//                return true;
//            }
//
//        }
        return false;
    }
}
