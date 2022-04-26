package com.example.comp7506_1.todolist.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.comp7506_1.todolist.R;
import com.example.comp7506_1.todolist.Bean.User;
import com.example.comp7506_1.todolist.Utils.NetWorkUtils;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends BasicActivity implements View.OnClickListener {
    private EditText mEtUserName;
    private EditText mEtPassWord;
    private Button mBtnRegister;
    private ImageView mBtnGoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_register);
        mEtUserName = (EditText) findViewById(R.id.register_name);
        mEtPassWord = (EditText) findViewById(R.id.register_pwd);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnGoLogin = (ImageView) findViewById(R.id.back_login);
        mBtnRegister.setOnClickListener(this);
        mBtnGoLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_register:

                final String username = mEtUserName.getText().toString();
                final String password = mEtPassWord.getText().toString();
                if(NetWorkUtils.isNetworkConnected(getApplication())){

                    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                        Toasty.info(RegisterActivity.this, "Please enter user name or password", Toast.LENGTH_SHORT, true).show();
                        return;
                    }

                    if (mEtUserName.length() < 4) {
                        Toasty.info(RegisterActivity.this, "The username must contain a maximum of four characters", Toast.LENGTH_SHORT, true).show();
                        return;
                    }

                    if (mEtPassWord.length() < 6) {
                        Toasty.info(RegisterActivity.this, "The password must contain at least six characters", Toast.LENGTH_SHORT, true).show();
                        return;
                    }

                    final User user = new User();

                    Log.i("register", "Uploaded successfully");
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setNickName(username);
                    user.setAutograph("Individuality signature");
//                                user.setImg(bmobFile);
                    user.setTotal(0);
                    user.signUp(new SaveListener<User>() {
                        @Override
                        public void done(User s, BmobException e) {if(e==null){
                            Toasty.success(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toasty.error(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT, true).show();
                            Log.i("register", e.getMessage());}
                        }
                    });
                }
                break;

            case R.id.back_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }


    private void setStatusBar(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
