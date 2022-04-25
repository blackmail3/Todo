package com.example.comp7506_1.todolist.Widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.comp7506_1.todolist.R;


public class SmileView extends View {

    private final int blue = 0xff4aadff;
    private int mWidth = 200;
    private int mHeight = 200;
    private int radius = 10;
    private int lineWidth = 2;
    private float eyeRadius;
    Paint paint, eyePaint;
    DrawFilter drawFilter;
    Path path, pathCircle, pathCircle2;
    PathMeasure pm, pm2;
    float length;
    float fraction = -1;
    long animDuration = 2000;
    ValueAnimator val;

    public SmileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SmileView);

        radius = mTypedArray.getInteger(R.styleable.SmileView_smil_radius, 15);
        lineWidth = mTypedArray.getInteger(R.styleable.SmileView_smil_line_width, 5);
        animDuration = mTypedArray.getInteger(R.styleable.SmileView_smil_duration, 2000);

        mTypedArray.recycle();


        initPaint(context);
    }

    private void initPaint(Context context) {
        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG);
        lineWidth = dip2px(context, lineWidth);
        radius = dip2px(context, radius);
        path = new Path();
        pathCircle = new Path();
        pathCircle2 = new Path();

        pathCircle.addCircle(0, 0, radius, Path.Direction.CW);
        pathCircle2.addCircle(0, 0, radius, Path.Direction.CW);

        Matrix m = new Matrix();
        m.postRotate(90);
        pathCircle.transform(m);


        paint = new Paint();

        eyePaint = new Paint();

        paint.setColor(blue);
        eyePaint.setColor(blue);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        eyePaint.setStrokeWidth(lineWidth);

        paint.setStrokeCap(Paint.Cap.ROUND);

        pm = new PathMeasure();
        pm.setPath(pathCircle, false);
        pm2 = new PathMeasure();
        pm2.setPath(pathCircle2, false);


        length = pm.getLength();
        eyeRadius = (float) (lineWidth / 2.0 + lineWidth / 5.0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            if (changed) {
                mWidth = right - left;
                mHeight = bottom - top;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(drawFilter);
        canvas.translate(mWidth / 2, mHeight / 2);
        if (fraction == -1 || val == null || (val != null && !val.isRunning()))
            first(canvas);

        if (0 < fraction && fraction < 0.75) {
            drawCircle(canvas);
        }

        if (fraction > 1.0 * 3 / 8 && fraction < 1.0 * 6 / 4) {
            drawOneEye(canvas, 0);
        }

        if (fraction > 1.0 * 5 / 8 && fraction < 1.0 * 6 / 4) {
            drawOneEye(canvas, 1);
        }

        if (fraction >= 0.75 && fraction <= 5.0 / 4) {
            drawFace(canvas);
        }

        if (fraction >= 5.0 / 4 && fraction <= (5.0 / 4 + 1.0 / 4)) {
            rotateFace(canvas);
        }
        if (fraction >= 6.0 / 4) {
            drawLastFact(canvas);
        }
    }

    public void drawFace(Canvas canvas) {
        path = null;
        path = new Path();
        // Save the length of the path according to the time. The value range is (0, length/2).
        pm2.getSegment(0, (float) (length * (fraction - 0.75)), path, true);
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpectMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpectSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpectMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpectSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpectMode == MeasureSpec.AT_MOST
                && heightSpectMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpectMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpectSize);
        } else if (heightSpectMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpectSize, mHeight);
        }
    }

    /**
     * Smile
     *
     * @param canvas
     */
    private void first(Canvas canvas) {
        pm2.getSegment(10, length / 2 - 10, path, true);
        canvas.drawPath(path, paint);
        path = new Path();
        drawEye(canvas);
    }

    /**
     * Eyes
     *
     * @param canvas
     */
    public void drawEye(Canvas canvas) {
        float x = (float) ((radius) * Math.cos(Math.PI * 45 / 180));
        float y = x;
        canvas.drawCircle(-x, -y, eyeRadius, eyePaint);
        canvas.drawCircle(x, -y, eyeRadius, eyePaint);
    }

    /**
     * Draw a moving circle from the bottom for 0-3/4 of the time
     * that is, starting from 270 degrees and going counterclockwise to 0 degrees
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        float degree = 270 - 270 * 4 / 3 * fraction;
        float x = (float) ((radius) * Math.cos(Math.PI * degree / 180));
        float y = -(float) ((radius) * Math.sin(Math.PI * degree / 180));
        canvas.drawCircle(x, y, eyeRadius, eyePaint);
    }

    /* @param canvas
        * @param pos
        */
    public void drawOneEye(Canvas canvas, int pos) {
        float x = (float) ((radius) * Math.cos(Math.PI * 45 / 180));
        float y = x;
        if (pos == 0) {
            canvas.drawCircle(-x, -y, eyeRadius, eyePaint);
        } else if (pos == 1) {
            canvas.drawCircle(x, -y, eyeRadius, eyePaint);
        }
    }

    public void rotateFace(Canvas canvas) {
        path = null;
        path = new Path();
        pm2.getSegment((float) (length * (fraction - 5.0 / 4)), (float) (length * (fraction - 5.0 / 4) + length * 0.5), path, true);
        canvas.drawPath(path, paint);
    }

    public void drawLastFact(Canvas canvas) {
        path = null;
        path = new Path();
        pm.getSegment((float) (1.0 / 4 * length + 3.0 / 2 * (fraction - 3.0 / 2) * length), (float) (length / 2.0 + length / 8.0 + (fraction - 3.0 / 2) * length), path, true);
        canvas.drawPath(path, paint);
    }

    public void performAnim() {
        //The ratio of time calculated above, which adds up to 2, is 2 weeks, so I'm going to set it to (0,2).
        val = ValueAnimator.ofFloat(0, 2);
        val.setDuration(animDuration);
        val.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                fraction = (float) arg0.getAnimatedValue();
                postInvalidate();
            }
        });
        val.setRepeatCount(ValueAnimator.INFINITE);
        val.start();
        val.setRepeatMode(ValueAnimator.RESTART);
    }


    public void setDuration(long animDuration) {
        this.animDuration = animDuration;
    }

    public void cancelAnim() {
        fraction = 0;
        if (val != null) {
            val.cancel();
        }
        postInvalidate();
    }

}
