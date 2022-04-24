package com.example.comp7506.todolist.Utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CountDownTimer {

    public interface OnCountDownTickListener {

        void onCountDownTick(long millisUntilFinished);
        void onCountDownFinish();

    }


    private final long mMillisInFuture;


    private final long mCountdownInterval;


    private long mStopTimeInFuture;

    private long mMillisUntilFinished;

    private boolean mPaused = false;

    private boolean mIsRunning = false;

    private Date mStartTime;

    private OnCountDownTickListener mOnCountDownTickListener;

    public CountDownTimer(long millisInFuture) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = 1000;
    }

    public void setOnChronometerTickListener(OnCountDownTickListener listener) {
        mOnCountDownTickListener = listener;
    }

    public synchronized final CountDownTimer start() {
        if (mMillisInFuture <= 0) {
            onFinish();
        } else {
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
            mStartTime = new Date();
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG), 1);
            mIsRunning = true;
            mPaused = false;
        }

        return this;
    }

    public final void pause() {
        mMillisUntilFinished = mStopTimeInFuture - SystemClock.elapsedRealtime();
        mIsRunning = false;
        mPaused = true;
    }

    public long resume() {
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisUntilFinished;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        mIsRunning = true;
        mPaused = false;
        return mMillisUntilFinished;
    }

    public final void cancel() {
        mHandler.removeMessages(MSG);
        mIsRunning = false;
    }

    private void onTick(long millisUntilFinished) {
        if (mOnCountDownTickListener != null) {
            mOnCountDownTickListener.onCountDownTick(millisUntilFinished);
        }
    }

    private void onFinish() {
        mIsRunning = false;

        if (mOnCountDownTickListener != null) {
            mOnCountDownTickListener.onCountDownFinish();
        }
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public Date getStartTime() {
        return mStartTime;
    }

    public long getMinutesInFuture() {
        return TimeUnit.MILLISECONDS.toMinutes(mMillisInFuture);
    }

    private static final int MSG = 1;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (CountDownTimer.this) {
                if (!mPaused) {
                    final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                    if (millisLeft <= 0) {
                        onFinish();
                    } else {
                        long lastTickStart = SystemClock.elapsedRealtime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval;

                        sendMessageDelayed(obtainMessage(MSG), delay);
                    }
                }
            }
        }
    };
}
