package com.jason.saliencycamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import imageProcessing.LSH;
import imageProcessing.commonImageProcessing;


public class ImageViewActivity extends Activity {
    private ImageView imageView;
    private ImageView grayImageView;
    private TextView textView;
    private Button calculateButton;

    private String picture1Uri = "";
    private String picture2Uri = "";
    private String drawable1;
    private String drawable2;
    private Long timeDifference = Long.valueOf(0);
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmapIIF1;
    private Bitmap bitmapIIF2;

    @Override
    protected void onResume() {
        super.onResume();
        setImageBitmap(imageView, bitmap1);
        setImageBitmap(grayImageView, bitmapIIF1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        // Initialize
        Bundle bundle = this.getIntent().getExtras();
        picture1Uri = bundle.getString("picture1_URI");
        picture2Uri = bundle.getString("picture2_URI");
        timeDifference = bundle.getLong("timeDifference");

        imageView = (ImageView) findViewById(R.id.imageView);
        grayImageView = (ImageView) findViewById(R.id.grayImageView);
        textView = (TextView) findViewById(R.id.textView);
        calculateButton = (Button) findViewById(R.id.calculateButton);

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
                Log.d("Image1 Size", "X,Y is " + bitmap1.getWidth()+","+bitmap1.getHeight());
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
                Log.d("Image1 Size", "X,Y is " + bitmap1.getWidth()+","+bitmap1.getHeight());
                e.printStackTrace();
            }
        }

        Log.d("Image1 Size", "X,Y is " + bitmap1.getWidth()+","+bitmap1.getHeight());
        Log.d("Image2 Size", "X,Y is " + bitmap2.getWidth()+","+bitmap2.getHeight());
//        if (bitmap1 != null)
//            bitmapIIF1 = commonImageProcessing.toGrayScale(bitmap1);
//        if (bitmap2 != null)
//            bitmapIIF2 = commonImageProcessing.toGrayScale(bitmap2);

        // Test LSH.IIF
//        if (bitmap1 != null)
//            bitmapIIF1 = LSH.IIF(bitmap1);
//        if (bitmap2 != null)
//            bitmapIIF2 = LSH.IIF(bitmap2);

        // Test Watershed Segmentation
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        if (bitmap1 != null)
                            bitmapIIF1 = commonImageProcessing.SLIC(bitmap1);
                        if (bitmap2 != null)
                            bitmapIIF2 = bitmapIIF1;
                        setImageBitmap(imageView, bitmap1);
                        setImageBitmap(grayImageView, bitmapIIF1);
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        });


        // Set up the imageViews
        setImageBitmap(imageView, bitmap1);
        setImageBitmap(grayImageView, bitmapIIF1);
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        setImageBitmap(imageView, bitmap1);
                        setImageBitmap(grayImageView, bitmapIIF1);
//                        Log.d("ACTION_UP", (picture1.exists()) ? "Picture1 exist" : "Picture1 not exist");
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        setImageBitmap(imageView, bitmap2);
                        setImageBitmap(grayImageView, bitmapIIF2);
//                        Log.d("ACTION_DOWN", (picture2.exists()) ? "Picture2 exist " : "Picture1 not exist");
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        setImageBitmap(imageView, bitmap2);
                        setImageBitmap(grayImageView, bitmapIIF2);
//                        Log.d("ACTION_DOWN ", (picture2.exists()) ? "Picture1 exist " : "Picture1 not exist");
                        break;
                    }
                }
                return true;
            }
        };
        imageView.setOnTouchListener(onTouchListener);
        grayImageView.setOnTouchListener(onTouchListener);

        // Setup the followings
        textView.setText(timeDifference + " ms");
        calculateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ImageViewActivity.this, ResultImageViewActivity.class);

                        Bundle bundle = new Bundle();
                        if(picture1Uri!=null)
                            bundle.putString("picture1_URI", picture1Uri);
                        else
                            bundle.putString("drawable1", drawable1);

                        if(picture1Uri!=null)
                            bundle.putString("picture2_URI", picture2Uri);
                        else
                            bundle.putString("drawable2", drawable2);

                        intent.putExtras(bundle);
                        intent.putExtra("date", new Date().getTime());
                        startActivity(intent);
                    }
                }
        );
    }

    private void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_view, menu);
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
//        bitmapIIF1.recycle();
//        bitmapIIF2.recycle();
//    }
}
