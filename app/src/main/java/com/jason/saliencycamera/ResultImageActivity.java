package com.jason.saliencycamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import imageProcessing.CommonImageProcessing;
import imageProcessing.LSH;


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
                    resultBitmap1 = bitmap1;
                    resultBitmap2 = bitmap2;
                    finishLayout();
                    break;
                case CommonImageProcessing.SLIC:
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status) {
                                case LoaderCallbackInterface.SUCCESS: {
                                    if (bitmap1 != null)
                                        resultBitmap1 = CommonImageProcessing.SLIC(bitmap1, getApplicationContext());
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
                case CommonImageProcessing.SaliencyDetection_withoutMD:
                    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
                        @Override
                        public void onManagerConnected(int status) {
                            switch (status) {
                                case LoaderCallbackInterface.SUCCESS: {
                                    if (bitmap1 != null && bitmap2 != null)
                                        resultBitmap1 = CommonImageProcessing.roughDepthMap(bitmap1, bitmap2);
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
                    resultBitmap1 = bitmap1;
                    resultBitmap2 = bitmap2;
                    finishLayout();
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
