package com.example.comp7506_1.todolist.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp7506_1.todolist.Dao.ClockDao;
import com.example.comp7506_1.todolist.R;
import com.example.comp7506_1.todolist.Bean.Clock;
import com.example.comp7506_1.todolist.Bean.User;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import es.dmoral.toasty.Toasty;


/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class StatisticFragment extends Fragment {

    private User user;
    private int tdTimes = 0;
    private int tdDuration = 0;
    private int allTimes = 0;
    private int allDuration = 0;
    private TextView todayDurations;
    private TextView todayTimes;
    private TextView amountDurations;
    private TextView amountTimes;

    public StatisticFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        todayDurations = view.findViewById(R.id.schedule_today_durations);
        todayTimes = view.findViewById(R.id.schedule_today_times);
        amountDurations = view.findViewById(R.id.schedule_amount_durations);
        amountTimes = view.findViewById(R.id.schedule_amount_times);

        setData();

        return view;
    }

    public void setData(){


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        user = User.getCurrentUser(User.class);

                        final BmobQuery<Clock> getToday = new BmobQuery<Clock>();
                        getToday.addWhereEqualTo("user",user);
                        getToday.addWhereEqualTo("date_add", ClockDao.formatDate(new Date()));
                        getToday.findObjects(new FindListener<Clock>() {
                            @Override
                            public void done(List<Clock> list, BmobException e) {
                                if (e==null){
                                    Log.i("Clock", "Number of data: " +list.size());
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
                                            Log.i("Clock", "Tomato Number: " + tdTimes);
                                            Log.i("Clock", "Cumulative Time: " + tdDuration);
                                        }
                                    }

                                } else {
//                                    Toasty.error(getActivity(), "Failed to connect to internet"+ e.getMessage(), Toast.LENGTH_SHORT, true).show();
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
                                    Log.i("Clock", "Number of data: "  +list.size());
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
                                            Log.i("Clock", "Tomato Number: " + allTimes);
                                            Log.i("Clock", "Cumulative Time: " + allDuration);
                                        }
                                    }

                                } else {
//                                    Toasty.error(getActivity(), "Failed to connect to internet"+ e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                }
                            }
                        });




                    }
                });
            }
        }).start();
    }


}
