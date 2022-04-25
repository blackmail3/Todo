package com.example.comp7506_1.todolist.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.example.comp7506_1.todolist.R;
import com.example.comp7506_1.todolist.Dao.ClockDao;
import com.example.comp7506_1.todolist.Bean.Clock;
import com.example.comp7506_1.todolist.Bean.User;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;

public class ScheduleActivity extends AppCompatActivity {

    private User user;
    private int tdTimes = 0;
    private int tdDuration = 0;
    private int allTimes = 0;
    private int allDuration = 0;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        toolbar = (Toolbar) findViewById(R.id.schedule_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView todayDurations = (TextView)findViewById(R.id.schedule_today_durations);
        final TextView todayTimes = (TextView)findViewById(R.id.schedule_today_times);
        final TextView amountDurations = (TextView)findViewById(R.id.schedule_amount_durations);
        final TextView amountTimes = (TextView)findViewById(R.id.schedule_amount_times);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ScheduleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        user = User.getCurrentUser(User.class);
                        ClockDao adapter = new ClockDao(ScheduleActivity.this);

                        BmobQuery<Clock> getToday = new BmobQuery<Clock>();
                        getToday.addWhereEqualTo("user",user);
                        getToday.addWhereEqualTo("date_add", ClockDao.formatDate(new Date()));
                        getToday.findObjects(new FindListener<Clock>() {
                            @Override
                            public void done(List<Clock> list, BmobException e) {
                                if (e==null){
                                    Log.i("Clock", "查询到: " +list.size()+" 条数据");
                                    if (list.size()==0){
                                        todayDurations.setText("0");
                                        todayTimes.setText("0");
                                    }
                                    for (Clock clock : list){
                                        if (clock.getEnd_time()!=null){
                                            tdTimes++;
                                            tdDuration += clock.getDuration();
                                            todayDurations.setText(String.valueOf(tdDuration));
                                            todayTimes.setText(String.valueOf(tdTimes));
                                            Log.i("Clock", "番茄钟个数：" + tdTimes);
                                            Log.i("Clock", "累计时间： " + tdDuration);
                                        }
                                    }

                                } else {
                                    Toasty.info(ScheduleActivity.this, "查询网络数据失败"+ e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });


                        // 累计数据
                        BmobQuery<Clock> allAmount = new BmobQuery<Clock>();
                        allAmount.addWhereEqualTo("user", user);
                        allAmount.findObjects(new FindListener<Clock>() {
                            @Override
                            public void done(List<Clock> list, BmobException e) {
                                if (e==null){
                                    Log.i("Clock", "查询到: " +list.size()+" 条数据");
                                    if (list.size()==0){
                                        amountDurations.setText("0");
                                        amountTimes.setText("0");
                                    }
                                    for (Clock clock : list){
                                        if (clock.getEnd_time()!=null){
                                            allTimes++;
                                            allDuration += clock.getDuration();
                                            amountDurations.setText(String.valueOf(allDuration));
                                            amountTimes.setText(String.valueOf(allTimes));
                                            Log.i("Clock", "番茄钟个数：" + allTimes);
                                            Log.i("Clock", "累计时间： " + allDuration);
                                        }
                                    }

                                } else {
                                    Toasty.info(ScheduleActivity.this, "查询网络数据失败"+ e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });




                    }
                });
            }
        }).start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
