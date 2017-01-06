package com.src.mm.cameradrawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Manish on 04-01-2017.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private String TAG="CameraPreview";
    public List<List<Dot>> mDots = new ArrayList<List<Dot>>();
    private Paint paint;

    public CameraPreview(Context context) {
        super(context);
        init();
    }
    public CameraPreview(Context context,
                         AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mCamera = getCameraInstance();
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.setBackgroundColor(Color.TRANSPARENT);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);


    }


    public void surfaceCreated(SurfaceHolder holder) {
      startCamera();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDots.add(new ArrayList<Dot>());
                mDots.get(mDots.size()-1).add(new Dot(event.getX(), event.getY()));
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mDots.get(mDots.size()-1).add(new Dot(event.getX(), event.getY()));
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mDots.get(mDots.size()-1).add(new Dot(event.getX(), event.getY()));
                this.invalidate();
                break;
        }
        return true;

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(List<Dot> dots : mDots){
            for(int i = 0; i < dots.size(); i++){
                if(i - 1 == -1)
                    continue;
                canvas.drawLine(dots.get(i - 1).X, dots.get(i - 1).Y, dots.get(i).X, dots.get(i).Y, paint);
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null){

            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e){

        }
        startCamera();

    }

    private void startCamera(){
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    private Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){

        }
        return c;
    }
    public Camera getCameraHandle(){
        return mCamera;
    }
    public List<List<Dot>> getGesture(){
        return mDots;
    }

    public void clearGesture(){
        mDots = new ArrayList<List<Dot>>();
        this.invalidate();
        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
