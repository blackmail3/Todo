package com.example.comp7506.todolist.Widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ClockView extends View {  //Tomato Clock

    private int centerX;
    private int centerY;
    private int radius;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  //ANTI_ALIAS_FLAG:anti-aliasing
    private Paint timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mColor = Color.parseColor("#D1D1D1");
    private RectF mRectF = new RectF();
    public static final float START_ANGLE = -90;
    public static final int MAX_TIME = 60;
    private float sweepVelocity = 0;
    private String textTime = "00:00";

    private int time;

    //Tomato
    private static int tomato;

    //countdown
    private int countdownTime;
    private float touchX;
    private float touchY;
    private float offsetX;
    private float offsetY;
    private boolean isStarted;
    ValueAnimator valueAnimator;


    CountDownTimer timer;

    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {  //重写带接收xml属性信息的构造方法
        super(context, attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { //defStyleAttr - default
        super(context, attrs, defStyleAttr);
    }

    public static float dpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics(); //获取屏幕参数
        return dp * metrics.density;  //density - the logical pixel density of the display，density = dpi(dots per inch) / 160,像素 = dp*density
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        centerX = width / 2;
        centerY = height / 2;
        radius = (int) dpToPixel(120);  // 3/4 inch
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) { //Override onDraw
        super.onDraw(canvas);
        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        canvas.save(); //save canvas
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE); //Paint.Style.STROKE
        mPaint.setStrokeWidth(dpToPixel(5));
        canvas.drawCircle(centerX, centerY, radius, mPaint);
        canvas.restore();
        //gray
        canvas.save();
        mPaint.setColor(mColor);
        canvas.drawArc(mRectF, START_ANGLE, 360 * sweepVelocity, false, mPaint);//useCenter:false - do not need to draw the arc line to the center of the circle
        canvas.restore();
        //Time
        canvas.save();
        timePaint.setColor(Color.BLACK);
        timePaint.setStyle(Paint.Style.FILL); //Paint.Style.FILL
        timePaint.setTextSize(dpToPixel(40));
        canvas.drawText(textTime, centerX - timePaint.measureText(textTime) / 2,
                centerY - (timePaint.ascent() + timePaint.descent()) / 2, timePaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isStarted) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();
        boolean isContained = isContained(x, y);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: //click
                if (isContained) {
                    touchX = x;
                    touchY = y;
                }
                break;
            case MotionEvent.ACTION_MOVE: //sliding
                if (isContained) {
                    offsetX = x - touchX;
                    offsetY = y - touchY;
                    time = (int) (offsetY / 2 / radius * MAX_TIME);
                    if (time <= 0) {
                        time = 0;
                        tomato = 0;
                    }
                    else if(time < 25) tomato = 0;
                    else if(time < 50) tomato = 1;
                    else tomato = 2;
                    textTime = formatTime(time);
                    countdownTime = time * 60;
                    invalidate(); //Refresh
                }
                break;
        }
        return true;
    }

    private boolean isContained(float x, float y) {
        if (Math.sqrt((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) > radius) {
            return false;
        } else {
            return true;
        }
    }

    private String formatTime(int time) {
        StringBuilder sb = new StringBuilder();
        if (time < 10) {
            sb.append("0" + time + ":00");
        } else {
            sb.append(time + ":00");
        }
        return sb.toString();
    }

    private String formatCountdownTime(int countdownTime) {
        StringBuilder sb = new StringBuilder();
        int minute = countdownTime / 60;
        int second = countdownTime - 60 * minute;
        if (minute < 10) {
            sb.append("0" + minute + ":");
        } else {
            sb.append(minute + ":");
        }
        if (second < 10) {
            sb.append("0" + second);
        } else {
            sb.append(second);
        }
        return sb.toString();
    }

    public void start(){
        //Determines whether the timer has started or ended
        if (countdownTime == 0 || isStarted) {
            return;
        }

        isStarted = true;

        valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.setDuration(countdownTime * 1000);
        valueAnimator.setInterpolator(new LinearInterpolator()); // During countdownTime*1000ms, float value increased from 0 to 1
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepVelocity = (float) animation.getAnimatedValue();
                mColor = Color.parseColor("#D1D1D1");
                invalidate();
            }
        });
        valueAnimator.start();

        timer = new CountDownTimer(countdownTime * 1000 + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTime = (countdownTime * 1000 - 1000) / 1000;
                textTime = formatCountdownTime(countdownTime);
                invalidate();
            }

            //Finish
            @Override
            public void onFinish() {
                mColor = Color.BLACK;
                sweepVelocity = 0;
                isStarted = false;
                invalidate();
            }
        }.start();
    }

    public void abandon(){
        timer.onFinish();
        timer.cancel();
        valueAnimator.cancel();
        textTime = "00:00";

        invalidate();
    }

    public void setTime(int time){
        this.time = time;
    }

    public void setCountdownTime(int countdownTime){
        this.countdownTime = countdownTime;
    }
}
