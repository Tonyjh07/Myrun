package com.example.myrun;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.Collections;

public class CircleView extends AppCompatImageView {

    private Paint mPaint;
    private Bitmap mBitmap;

    public CircleView(@NonNull Context context) {
        this(context,null);
    }

    public CircleView(@NonNull Context context,@Nullable AttributeSet attrs){
        super(context,attrs);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Drawable drawable = getDrawable();
        if(drawable instanceof BitmapDrawable){
            mBitmap=((BitmapDrawable)drawable).getBitmap();
        }
    }

    public void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(mBitmap!=null){
            drawCircle(canvas);
        }else{
            super.onDraw(canvas);
        }

    }

    private void drawCircle(Canvas canvas) {

        BitmapShader bitmapShader = new BitmapShader(mBitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        float scaleX = getWidth()/(float)mBitmap.getWidth();
        float scaleY = getHeight()/(float)mBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX,scaleY);
        bitmapShader.setLocalMatrix(matrix);
        mPaint.setShader(bitmapShader);

        canvas.drawCircle(getWidth()/2f,getHeight()/2f,
                Math.min(getWidth()/2f,getHeight()/2f),mPaint);



    }
}
