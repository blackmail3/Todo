package com.example.comp7506_1.todolist.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.comp7506_1.todolist.R;
import com.example.comp7506_1.todolist.Receiver.NetworkReceiver;
import com.example.comp7506_1.todolist.Service.AlarmService;
import com.example.comp7506_1.todolist.Utils.FileUtils;
import com.example.comp7506_1.todolist.Utils.NetWorkUtils;
import com.example.comp7506_1.todolist.Utils.SPUtils;
import com.example.comp7506_1.todolist.Bean.User;

import cn.bmob.v3.Bmob;
import site.gemus.openingstartanimation.NormalDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class SplashActivity extends BasicActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private static final String APP_ID = "1c54d5b204e98654778c77547afc7a66";
    private NetworkReceiver networkReceiver;
    private FileUtils fileUtils;
    private static final String KEY_VIBRATE = "vibrator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        fileUtils = new FileUtils();
        fileUtils.copyData(getApplicationContext());
        SPUtils.put(this,"isFocus",false);

        if (NetWorkUtils.isNetworkConnected(getApplication())){
            Bmob.resetDomain("https://open3.bmob.cn/8/");
            Bmob.initialize(getApplication(), APP_ID);
        }


        Resources res = this.getResources();
        startService(new Intent(this, AlarmService.class));
        OpeningStartAnimation openingStartAnimation = new OpeningStartAnimation.Builder(this)
                .setDrawStategy(new NormalDrawStrategy())
                .setAppIcon(res.getDrawable(R.drawable.ic_launcher))
                .setColorOfAppName(R.color.icon_color)
                .setAppStatement("Cease to struggle, Cease to life")
                .setColorOfAppStatement(R.color.icon_color)
                .create();
        openingStartAnimation.show(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if (User.getCurrentUser(User.class)==null){
                    Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }

            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
