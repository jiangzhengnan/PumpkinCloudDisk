package com.example.pumpkinonlinedisk.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircleProgressView extends View {

    private int mMeasureHeigth;
    private int mMeasureWidth;

    private Paint mCirclePaint;
    private float mCircleXY;
    private float mRadius;

    private Paint mArcPaint;
    private RectF mArcRectF;
    private float mSweepAngle;
    private float mSweepValue = 66;

    private Paint mTextPaint;
    private static String mShowText;
    private float mShowTextSize;
    
    private double mRandom;
    private static float num;

    public CircleProgressView(Context context, AttributeSet attrs,
                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleProgressView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        mMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
        mMeasureHeigth = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mMeasureWidth, mMeasureHeigth);
        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制圆
        canvas.drawCircle(mCircleXY, mCircleXY, mRadius, mCirclePaint);
        mRandom = Math.random();
        // 绘制弧线
 
        canvas.drawArc(mArcRectF, 270, num, false, mArcPaint);
        // 绘制文字
         
        canvas.drawText(mShowText, 0, mShowText.length(),
                mCircleXY, mCircleXY + (mShowTextSize / 4), mTextPaint);
        /* //设置延迟
        postInvalidateDelayed(100); */
    }

    private void initView() {
        float length = 0;
        if (mMeasureHeigth >= mMeasureWidth) {
            length = mMeasureWidth;
        } else {
            length = mMeasureHeigth;
        }

        mCircleXY = length / 2;
        mRadius = (float) (length * 0.5 / 2);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(getResources().getColor(
                android.R.color.darker_gray));

        mArcRectF = new RectF(
                (float) (length * 0.1),
                (float) (length * 0.1),
                (float) (length * 0.9),
                (float) (length * 0.9));
        mSweepAngle = (mSweepValue / 100f) * 360f;
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(getResources().getColor(
                android.R.color.holo_blue_bright));
        mArcPaint.setStrokeWidth((float) (length * 0.1));
        mArcPaint.setStyle(Style.STROKE);

   /*     mShowText = "ee";
        num = 22;*/
        mShowTextSize = setShowTextSize();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mShowTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        
        if (num == 360) {
            mCirclePaint.setColor(getResources().getColor(
                    android.R.color.holo_blue_bright));
			
        }
    }

    private float setShowTextSize() {
        this.invalidate();
        return 20;
    }

    private String setShowText(String text) {
        this.invalidate();
        return text;
    }
    
    public  void setshuju(float num, String text) {
    	  this.invalidate();
    	this.num = num;
    	mShowText = text;
    	Log.d("viewwwwwwwwwwwwwwwww", mShowText + "|||"+String.valueOf(this.num));
    	postInvalidate();
    }

    public void forceInvalidate() {
        this.invalidate();
    }

    public void setSweepValue(float sweepValue) {
        if (sweepValue != 0) {
            mSweepValue = sweepValue;
        } else {
            mSweepValue = 25;
        }
        this.invalidate();
    }
}
