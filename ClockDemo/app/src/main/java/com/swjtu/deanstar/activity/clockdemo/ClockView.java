package com.swjtu.deanstar.activity.clockdemo;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;
import java.util.Calendar;



/**
 * Created by yhp5210 on 2016/12/21.
 */

public class ClockView extends View implements Runnable{

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private Paint mOutterPaint,mNumberPaint,mCenterBlackCirclePaint
            ,mCenterYellowCirclePaint,mLogoPaint;
    private Context mContext;
    private int mWidth,mHight;
    private static final float mOutterLineWidth = 40.0f;
    private static final float mTextSize = 100;
    private static final float mCenterBlackCircleSize = 20.0f;
    private static final float mCenterYellowCircleSize = 10.0f;
    private static final int mBitmapWidth = 44;
    private static final int mBitmapHeight = 22;
    private float mSecondLineLen; //秒针长度
    private float mHourLineLen;  //时针长度
    private float mMinuteLineLen;//分针长度

    private boolean isInited = false;
    private int hour = 0,minute = 0,second = 0;

    public ClockView(Context context) {
        super(context);
        initResources();
        mContext = context;
    }
    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initResources();
        mContext = context;
    }
    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initResources();
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isInited){
            canvas.drawCircle(mWidth/2,mHight/2,(mWidth-mOutterLineWidth*2)/2,mOutterPaint);//画表框
            canvas.translate(0,0);
            canvas.translate(mWidth/2,mHight/2+mOutterLineWidth);
            canvas.drawCircle(0,-mOutterLineWidth,mCenterBlackCircleSize,mCenterBlackCirclePaint);
            canvas.drawCircle(0,-mOutterLineWidth,mCenterYellowCircleSize,mCenterYellowCirclePaint);
            Bitmap logo = decodeSampledBitmapFromResource(getResources(),R.drawable.bg,
                    mBitmapWidth, mBitmapHeight);
            Matrix matrix = new Matrix();
            matrix.postTranslate(-mBitmapWidth/2,-mOutterLineWidth);
            canvas.drawBitmap(logo,matrix,mLogoPaint);
            //画数字
            for(int i=0;i<12;i++){

                int number = (12 + i)%12;
                if(0 == number){
                    number = 12;
                }
                String sNumber = String.valueOf(number);
                float textX = (float)Math.cos((270+i*30)/180.0*Math.PI)*(mWidth-mOutterLineWidth*2-mTextSize*1.5f)/2;
                float textY = (float)Math.sin((270+i*30)/180.0*Math.PI)*(mHight-mOutterLineWidth*2-mTextSize*1.5f)/2;
                canvas.drawText(sNumber,textX,textY,mNumberPaint);
            }
            //isInited = true;
            postDelayed(this,1000);
        }

        //画时针和分针
        float hourEndX = mHourLineLen *(float)Math.cos((270+(hour%12)*30+minute/60.0*30)/180.0*Math.PI);
        float hourEndY = mHourLineLen *(float)Math.sin((270+(hour%12)*30+minute/60.0*30)/180.0*Math.PI);
        float minuteEndX = mMinuteLineLen *(float)Math.cos((270+minute*6)/180.0*Math.PI);
        float minuteEndY = mMinuteLineLen *(float)Math.sin((270+minute*6)/180.0*Math.PI);
        canvas.drawLine(0,-mOutterLineWidth,minuteEndX,minuteEndY,mCenterBlackCirclePaint);
        canvas.drawLine(0,-mOutterLineWidth,hourEndX,hourEndY,mCenterBlackCirclePaint);
        //画秒针
        float secondEndX = mSecondLineLen *(float)Math.cos((270+second*6)/180.0*Math.PI);
        float secondEndY = mSecondLineLen *(float)Math.sin((270+second*6)/180.0*Math.PI);
        float[] secondLine = new float[]{0,-mOutterLineWidth,secondEndX,secondEndY};
        canvas.drawLines(secondLine,mCenterYellowCirclePaint);
        canvas.restore();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = ViewUitl.getSize(widthMeasureSpec,WIDTH);
        mHight = ViewUitl.getSize(heightMeasureSpec,HEIGHT);
        mSecondLineLen = Math.min(mWidth,mHight)*0.35f;
        mMinuteLineLen = Math.min(mWidth,mHight)*0.30f;
        mHourLineLen= Math.min(mWidth,mHight)*0.25f;
        setMeasuredDimension(mWidth,mHight);

    }


    public void initResources(){

        mOutterPaint = new Paint();
        mOutterPaint.setStrokeWidth(mOutterLineWidth);
        mOutterPaint.setColor(Color.BLACK);
        mOutterPaint.setStyle(Paint.Style.STROKE);
        mNumberPaint = new Paint();
        mNumberPaint.setColor(Color.BLACK);
        mNumberPaint.setTextAlign(Paint.Align.CENTER);
        mNumberPaint.setTextSize(mTextSize);
        mCenterBlackCirclePaint = new Paint();
        mCenterBlackCirclePaint.setColor(Color.BLACK);
        mCenterBlackCirclePaint.setStyle(Paint.Style.FILL);
        mCenterBlackCirclePaint.setStrokeWidth(mCenterBlackCircleSize);
        mCenterYellowCirclePaint = new Paint();
        mCenterYellowCirclePaint.setStrokeWidth(mCenterYellowCircleSize);
        mCenterYellowCirclePaint.setColor(Color.parseColor("#FF7F24"));
        mCenterYellowCirclePaint.setStyle(Paint.Style.FILL);
        mCenterYellowCirclePaint.setAntiAlias(true);
        mLogoPaint = new Paint();
    }

    @Override
    public void run() {

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        System.out.println(hour+":"+minute+":"+second);
        invalidate();
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight){

        //首先设置inJustDecodeBounds = true来解码用来检查尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res,resId,options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res,resId,options);
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options,int reqWidth,int reqHeight){
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth){

            final int halfHeight = height/2;
            final int halfWidth = width/2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps     both
            // height and width larger than the requested height and width.
            while((halfHeight/inSampleSize) >= reqHeight
                    &&(halfWidth/inSampleSize) >= reqWidth){
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
