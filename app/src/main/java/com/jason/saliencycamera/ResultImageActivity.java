package com.jason.saliencycamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import imageProcessing.CommonImageProcessing;
import imageProcessing.LSH;
import imageProcessing.SLIC;
import imageProcessing.SlicBuilder;
import imageProcessing.SuperpixelImage;


public class ResultImageActivity extends Activity {
    private ImageView imageView;
    private ImageView resultImageView;
    private TextView textView;
    private Date oldDate;
    private Date newDate;
    private long timeDifference;

    private int action = 0;
    private String picture1Uri = "";
    private String picture2Uri = "";
    private String drawable1 = "";
    private String drawable2 = "";
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap resultBitmap1;
    private Bitmap resultBitmap2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_image_view);

        // Initialize
        Bundle bundle = this.getIntent().getExtras();
        action = bundle.getInt("ACTION");
        picture1Uri = bundle.getString("picture1_URI");
        picture2Uri = bundle.getString("picture2_URI");
        oldDate = new Date(this.getIntent().getLongExtra("date", -1));

        imageView = (ImageView) findViewById(R.id.inputImage);
        resultImageView = (ImageView) findViewById(R.id.resultImage);
        textView = (TextView) findViewById(R.id.textView);

        // Get the pictures
        if (picture1Uri != null) {
            final File picture1 = new File(picture1Uri);
            if (picture1.exists()) {
                bitmap1 = BitmapFactory.decodeFile(picture1.getAbsolutePath());
            } else
                bitmap1 = null;
        } else {
            drawable1 = bundle.getString("drawable1");
            try {
                bitmap1 = BitmapFactory.decodeStream(this.getAssets().open(drawable1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (picture2Uri != null) {
            final File picture2 = new File(picture2Uri);
            if (picture2.exists()) {
                bitmap2 = BitmapFactory.decodeFile(picture2.getAbsolutePath());
            } else
                bitmap2 = null;
        } else {
            drawable2 = bundle.getString("drawable2");
            try {
                bitmap2 = BitmapFactory.decodeStream(this.getAssets().open(drawable2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bitmap1 != null && bitmap2 != null) {
            switch(action){
                case CommonImageProcessing.LSHIIF:
                    resultBitmap1 = LSH.IIF(bitmap1);
                    resultBitmap2 = LSH.IIF(bitmap2);
                    finishLayout();
                    break;
                case CommonImageProcessing.MotionDetection:
                    final Bitmap IIFBitmap1 = LSH.IIF(bitmap1);
                    final Bitmap IIFBitmap2 = LSH.IIF(bitmap2);
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status) {
                                case LoaderCallbackInterface.SUCCESS: {
                                    if (bitmap1 != null)
                                        resultBitmap1 = CommonImageProcessing.motionDetection(IIFBitmap1, IIFBitmap2, IIFBitmap1);
                                    if(bitmap2 != null)
                                        resultBitmap2 = CommonImageProcessing.motionDetection(IIFBitmap1, IIFBitmap2, IIFBitmap2);
                                    finishLayout();
                                }
                                break;
                                default: {
                                    super.onManagerConnected(status);
                                }
                                break;
                            }
                        }
                    });
                    break;
                case CommonImageProcessing.SLIC:
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status) {
                                case LoaderCallbackInterface.SUCCESS: {
                                    if (bitmap1 != null && bitmap2 != null){
                                        resultBitmap1 = CommonImageProcessing.SLIC(bitmap1);
                                        resultBitmap2 = CommonImageProcessing.SLIC(bitmap2);
                                    }
                                    finishLayout();
                                }
                                break;
                                default: {
                                    super.onManagerConnected(status);
                                }
                                break;
                            }
                        }
                    });
                    break;
                case CommonImageProcessing.SaliencyDetection_withoutMD:
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status) {
                                case LoaderCallbackInterface.SUCCESS: {
                                    if (bitmap1 != null)
                                        resultBitmap1 = CommonImageProcessing.roughDepthMap(bitmap1, bitmap2);
                                    if(bitmap2 != null)
//                                        resultBitmap2 = CommonImageProcessing.diffMap(bitmap1,bitmap2);
                                        resultBitmap2 = resultBitmap1;
                                    finishLayout();
                                }
                                break;
                                default: {
                                    super.onManagerConnected(status);
                                }
                                break;
                            }
                        }
                    });
                    break;
                case CommonImageProcessing.SaliencyDetection_withMD:
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status) {
                                case LoaderCallbackInterface.SUCCESS: {
                                    if (bitmap1 != null && bitmap2 !=null){
                                        //Generate the IIF images
                                        Bitmap IIFBitmap1 = LSH.IIF(bitmap1);
                                        Bitmap IIFBitmap2 = LSH.IIF(bitmap2);

                                        //Calculate the optical flow using the IIF images
                                        MatOfPoint2f point1 = new MatOfPoint2f();
                                        MatOfPoint2f point2 = new MatOfPoint2f();
                                        MatOfByte resultStatus = new MatOfByte();
                                        CommonImageProcessing.opticalFlow(IIFBitmap1, IIFBitmap2, point1, point2, resultStatus);
//                                        CommonImageProcessing.perPixelOpticalFlow(IIFBitmap1, IIFBitmap2, point1, point2, resultStatus);

                                        //Work out the superpixel object for calculation
                                        SLIC slic = new SlicBuilder().buildSLIC();
                                        SuperpixelImage superpixel = slic.createSuperpixel(bitmap1);

                                        //Calculate the average displacement of each superpixel
                                        superpixel.calculateDisplacement(point1,point2, resultStatus);
//                                        superpixel.setDisplacement(point1,point2, resultStatus);

                                        //Use the superpixel object to calculate the roughDepthMap
                                        resultBitmap1 = CommonImageProcessing.motionCompensatedSaliencyDetection(bitmap1, bitmap2, superpixel);
//                                        resultBitmap1 = CommonImageProcessing.motionCompensatedSaliencyDetection2(bitmap1, bitmap2, superpixel);
                                        resultBitmap2 = resultBitmap1;
                                    }
                                    finishLayout();
                                }
                                break;
                                default: {
                                    super.onManagerConnected(status);
                                }
                                break;
                            }
                        }
                    });
                    break;
            }
        }
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        imageView.setImageBitmap(bitmap1);
                        resultImageView.setImageBitmap(resultBitmap1);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        imageView.setImageBitmap(bitmap2);
                        resultImageView.setImageBitmap(resultBitmap2);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        imageView.setImageBitmap(bitmap2);
                        resultImageView.setImageBitmap(resultBitmap2);
                        break;
                    }
                }
                return true;
            }
        };
        imageView.setOnTouchListener(onTouchListener);
        resultImageView.setOnTouchListener(onTouchListener);
    }

    private void finishLayout(){
        if(resultBitmap1!=null&&resultBitmap2!=null){
            newDate = new Date();
            timeDifference = newDate.getTime() - oldDate.getTime();
            imageView.setImageBitmap(bitmap1);
            resultImageView.setImageBitmap(resultBitmap1);
            textView.setText("used " + timeDifference + " ms");
            FileOutputStream out = null;
            try {
                // Get file name
                String filename = "";
                if (picture2Uri != null) {
                    final File picture2 = new File(picture2Uri);
                    if (picture2.exists()) {
                        filename = picture2.getName();
                    }
                } else {
                    filename = drawable2;
                }
                int pos = filename.lastIndexOf(".");
                if (pos > 0) {
                    filename = filename.substring(0, pos);
                }

                switch (action){
                    case CommonImageProcessing.LSHIIF:
                        filename+="_LSIIF";
                        break;
                    case CommonImageProcessing.MotionDetection:
                        filename+="_MotionDetection";
                        break;
                    case CommonImageProcessing.SLIC:
                        filename+="_SLIC";
                        break;
                    case CommonImageProcessing.SaliencyDetection_withoutMD:
                        filename+="_SaliencyDetection_withoutMD";
                        break;
                    case CommonImageProcessing.SaliencyDetection_withMD:
                        filename+="_SaliencyDetection_withMD";
                        break;
                }
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "SaliencyCameraResult");
                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("SaliencyCameraResult", "failed to create directory");
                    }
                }
                else{
                    out = new FileOutputStream(mediaStorageDir.getPath() + File.separator + filename+"_result"+".png");
                    resultBitmap1.compress(Bitmap.CompressFormat.PNG, 100, out);
                    // PNG is a lossless format, the compression factor (100) is ignored
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        releaseBitmap();
//    }
//
//    private void releaseBitmap() {
//        bitmap1.recycle();
//        bitmap2.recycle();
//        resultBitmap.recycle();
//    }
}
