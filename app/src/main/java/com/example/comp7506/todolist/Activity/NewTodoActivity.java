package com.example.comp7506.todolist.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.comp7506.todolist.Dao.ToDoDao;
import com.example.comp7506.todolist.R;
import com.example.comp7506.todolist.Service.AlarmService;
import com.example.comp7506.todolist.Utils.NetWorkUtils;
import com.example.comp7506.todolist.Utils.PermissionPageUtils;
import com.example.comp7506.todolist.Utils.SPUtils;
import com.example.comp7506.todolist.Bean.Todos;
import com.example.comp7506.todolist.Bean.User;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.lai.library.ButtonStyle;
import com.pl.voiceAnimation.VoiceAnimator;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import es.dmoral.toasty.Toasty;
import me.drakeet.materialdialog.MaterialDialog;

public class NewTodoActivity extends BasicActivity implements EventListener {

    private String todoTitle,todoDsc;
    private String todoDate = null, todoTime = null;
    private FloatingActionButton fab_ok;
    private TextView nv_todo_date,nv_todo_time,voice_result;
    private EditText nv_todo_title,nv_todo_dsc;
    private Switch nv_repeat;
    private int mYear,mMonth,mDay;
    private int mHour,mMin;
    private long remindTime;
    private Calendar ca;
    private static final String TAG = "time";
    private Toolbar toolbar;
    private int isRepeat = 0;
    private ImageView new_bg;
    private static int[] imageArray = new int[]{R.drawable.img_1,
            R.drawable.img_2,
            R.drawable.img_3,
            R.drawable.img_4,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_7,
            R.drawable.img_8,};
    private int imgId;
    private Todos todos;
    private FABProgressCircle fabProgressCircle;
    private MaterialDialog voice;
    private ButtonStyle done;
    private PermissionPageUtils permissionPageUtils;

    private int[] data={
            0,0,0,0,1,0,0,0,18,19,
            21,18,9,9,16,20,18,11,17,13,
            17,12,16,16,20,16,5,1,0,4,
            16,17,9,16,20,11,6,16,16,11,
            6,14,16,8,5,13,13,6,2,16,
            18,12,7,13,15,13,4,1,18,15,
            7,3,14,13,6,4,12,10,15,12,
            1,1,15,20,0,3,10,1,8,17};
    private VoiceAnimator voiceAnimator;
    private Button mic_title,mic_dsc;
    private boolean enableOffline = true;
    private EventManager manager;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_new_todo);
        toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ca = Calendar.getInstance();
        getDate();
        getTime();
        initView();
        initHeadImage();
        checkNotificationPermission();
    }

    private void initHeadImage(){

        Random random = new Random();
        imgId = imageArray[random.nextInt(8)];
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true);
        Glide.with(getApplicationContext())
                .load(imgId)
                .apply(options)
                .into(new_bg);

    }
    /**
     * 获取日期
     */
    private void getDate(){

        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取时间
     */
    private void getTime(){
        mHour = ca.get(Calendar.HOUR_OF_DAY);
        mMin = ca.get(Calendar.MINUTE);
    }

    private void initView() {

        fab_ok = (FloatingActionButton) findViewById(R.id.fab_ok);
        nv_todo_title = (EditText) findViewById(R.id.new_todo_title);
        nv_todo_dsc = (EditText) findViewById(R.id.new_todo_dsc);
        nv_todo_date = (TextView) findViewById(R.id.new_todo_date);
        nv_todo_time = (TextView) findViewById(R.id.new_todo_time);
        nv_repeat = (Switch) findViewById(R.id.repeat);
        new_bg = (ImageView) findViewById(R.id.new_bg);
        fabProgressCircle = (FABProgressCircle) findViewById(R.id.fabProgressCircle);
//        mic_title = (Button) findViewById(R.id.mic_title);
//        mic_dsc = (Button) findViewById(R.id.mic_dsc);

        fab_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todoDate==null){
                    Toasty.info(NewTodoActivity.this, "No date set", Toast.LENGTH_SHORT, true).show();
                } else if (todoTime==null) {
                    Toasty.info(NewTodoActivity.this, "No reminder time set", Toast.LENGTH_SHORT, true).show();

                } else {
                    fabProgressCircle.show();
                    todoTitle = nv_todo_title.getText().toString();
                    todoDsc = nv_todo_dsc.getText().toString();
                    Calendar calendarTime = Calendar.getInstance();
                    calendarTime.setTimeInMillis(System.currentTimeMillis());
                    calendarTime.set(Calendar.YEAR, mYear);
                    calendarTime.set(Calendar.MONTH, mMonth);
                    calendarTime.set(Calendar.DAY_OF_MONTH, mDay);
                    calendarTime.set(Calendar.HOUR_OF_DAY, mHour);
                    calendarTime.set(Calendar.MINUTE, mMin);
                    calendarTime.set(Calendar.SECOND, 0);
                    remindTime = calendarTime.getTimeInMillis();
                    Log.i(TAG, "时间是"+String.valueOf(remindTime));
                    //插入数据
                    User user = BmobUser.getCurrentUser(User.class);
                    todos = new Todos();
                    todos.setUser(user);
                    todos.setTitle(todoTitle);
                    todos.setDesc(todoDsc);
                    todos.setDate(todoDate);
                    todos.setTime(todoTime);
                    todos.setRemindTime(remindTime);
                    todos.setisAlerted(0);
                    todos.setIsRepeat(isRepeat);
                    todos.setImgId(imgId);
                    Date date = new Date(remindTime);
                    BmobDate bmobDate = new BmobDate(date);
                    todos.setBmobDate(bmobDate);

                    boolean isSync = (Boolean) SPUtils.get(getApplication(),"sync",true);
                    Log.i("ToDo", "isSync: " + isSync);

                    if (isSync){
                        //保存数据到Bmob
                        if(NetWorkUtils.isNetworkConnected(getApplication()) && User.getCurrentUser(User.class)!= null){
                            todos.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if(e==null){
                                        //插入本地数据库
                                        new ToDoDao(getApplicationContext()).create(todos);
                                        Log.i("bmob","保存到Bmob成功 "+ todos.getObjectId());
                                        Log.i("bmob","保存到本地数据库成功 "+ todos.getObjectId());
//                                        Intent intent = new Intent(NewTodoActivity.this, MainActivity.class);
//                                        setResult(2, intent);
                                        startService(new Intent(NewTodoActivity.this, AlarmService.class));
                                        finish();
                                    }else{
                                        Log.i("bmob","保存到Bmob失败："+e.getMessage());
                                    }
                                }
                            });

                        } else {
                            Toasty.info(NewTodoActivity.this, "请先登录", Toast.LENGTH_SHORT, true).show();
                        }
                    } else {
                        new ToDoDao(getApplicationContext()).create(todos);
//                        Intent intent = new Intent(NewTodoActivity.this, MainActivity.class);
//                        setResult(2, intent);
                        startService(new Intent(NewTodoActivity.this, AlarmService.class));
                        finish();
                    }

                }

            }
        });


        nv_todo_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(NewTodoActivity.this, onDateSetListener, mYear, mMonth, mDay);
                datePickerDialog.setCancelable(true);
                datePickerDialog.setCanceledOnTouchOutside(true);
                datePickerDialog.show();

            }
        });

        nv_todo_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(NewTodoActivity.this, onTimeSetListener, mHour,mMin, true);
                timePickerDialog.setCancelable(true);
                timePickerDialog.setCanceledOnTouchOutside(true);
                timePickerDialog.show();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nv_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    isRepeat = 1;
                } else {
                    isRepeat = 0;
                }

            }
        });


    }

    /**
     * 日期选择器对话框监听
     */
    public DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
//                todoDate = year+ "年"+(monthOfYear + 1) + "月" + dayOfMonth + "日";
                todoDate = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                nv_todo_date.setText(todoDate);
            }
        };

    /**
     * 时间选择对话框监听
     */
    public TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            mHour = hour;
            mMin = minute;
            if (minute < 10){
                todoTime = hour + ":" + "0" + minute;
            } else {
                todoTime = hour + ":" + minute;
            }
            nv_todo_time.setText(todoTime);
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    private void checkNotificationPermission(){
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplication());
        boolean isOpened = manager.areNotificationsEnabled();
        permissionPageUtils = new PermissionPageUtils(this);

        if (!isOpened){
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final MaterialDialog check = new MaterialDialog(this);
                check.setTitle("Reminder");
                check.setMessage("If the notification permission is not enabled, the todo reminder function will be affected. Manually enable the notification permission.");
                check.setPositiveButton("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", getPackageName());
                        intent.putExtra("app_uid", getApplicationInfo().uid);
                        startActivity(intent);
                        check.dismiss();
                    }
                });
                check.setCanceledOnTouchOutside(true);
                check.show();

            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                final MaterialDialog check = new MaterialDialog(this);
                check.setTitle("Reminder");
                check.setMessage("If the notification permission is not enabled, the todo reminder function will be affected. Manually enable the notification permission.");
                check.setPositiveButton("Enable", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        check.dismiss();
                    }
                });
                check.setCanceledOnTouchOutside(true);
                check.show();
            }
        }
    }

    @Override
    public void onEvent(String s, String s1, byte[] bytes, int i, int i1) {

    }
}
