package com.src.mm.cameradrawing;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class CaptureGesture extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private ProgressDialog pgDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_capture_gesture);
        createProgressDialog();
        Button takePic= (Button) findViewById(R.id.button_capture);
        mPreview=(CameraPreview) findViewById(R.id.camera_preview);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenShot();
            }
        });

    }
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Date now = new Date();
            File imageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "gestureCapture");
            if(!imageDir.exists()){
                imageDir.mkdir();
            }
            String imagePath =imageDir+ "/" + now + ".jpg";
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            Bitmap bmp1 = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap drawableBitmap = Bitmap.createScaledBitmap(bmp1, mPreview.getWidth(), mPreview.getHeight(), true);

            try {

                Canvas canvas = new Canvas(drawableBitmap);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.RED);

                for(List<Dot> dots : mPreview.getGesture()){
                    for(int i = 0; i < dots.size(); i++){
                        if(i - 1 == -1)
                            continue;
                        canvas.drawLine(dots.get(i - 1).X, dots.get(i - 1).Y, dots.get(i).X, dots.get(i).Y, paint);
                    }
                }
                File imageFile = new File(imagePath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                drawableBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.write(data);
                outputStream.flush();
                outputStream.close();
            } catch (Throwable e) {
                // Several error may come out with file handling or OOM
                e.printStackTrace();
            }
            mPreview.clearGesture();
         //   pgDialog.hide();

        }
    };
    private void takeScreenShot(){
     //   pgDialog.show();
        mCamera= mPreview.getCameraHandle();
        mCamera.takePicture(null,null,mPicture);



    }
    private void createProgressDialog() {
        pgDialog = new ProgressDialog(this);
        pgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pgDialog.setIndeterminate(true);
        pgDialog.setCancelable(false);


    }




}
