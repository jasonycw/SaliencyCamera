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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import imageProcessing.CommonImageProcessing;


public class ImageViewActivity extends Activity {
    private ImageView imageView;
    private Button DifferenceImage_button;
    private Button LSHIIF_button;
    private Button MotionDetection_button;
    private Button SLIC_button;
    private Button Saliency_withoutMD_button;
    private Button Saliency_withMD_button;
    private Button TestAll_button;

    private String picture1Uri = "";
    private String picture2Uri = "";
    private String drawable1;
    private String drawable2;
    private Long timeDifference = Long.valueOf(0);
    private Bitmap bitmap1;
    private Bitmap bitmap2;

    @Override
    protected void onResume() {
        super.onResume();
        setImageBitmap(imageView, bitmap1);
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
        TextView textView = (TextView) findViewById(R.id.textView);

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

        // Set up the imageViews
        setImageBitmap(imageView, bitmap1);
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        setImageBitmap(imageView, bitmap1);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        setImageBitmap(imageView, bitmap2);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        setImageBitmap(imageView, bitmap2);
                        break;
                    }
                }
                return true;
            }
        };
        imageView.setOnTouchListener(onTouchListener);

        // Setup the followings
        textView.setText(timeDifference + " ms");

        LSHIIF_button = (Button)findViewById(R.id.LSHIIF_button);
        LSHIIF_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startResultImageActivity(CommonImageProcessing.LSHIIF);
                    }
                }
        );

        DifferenceImage_button = (Button)findViewById(R.id.difference_button);
        DifferenceImage_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startResultImageActivity(CommonImageProcessing.DifferenceImage);
                    }
                }
        );

        MotionDetection_button = (Button)findViewById(R.id.MotionDetection_button);
        MotionDetection_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startResultImageActivity(CommonImageProcessing.MotionDetection);
                    }
                }
        );

        SLIC_button = (Button)findViewById(R.id.SLIC_button);
        SLIC_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startResultImageActivity(CommonImageProcessing.SLIC);
                    }
                }
        );

        Saliency_withoutMD_button = (Button)findViewById(R.id.Saliency_withoutMD_button);
        Saliency_withoutMD_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startResultImageActivity(CommonImageProcessing.SaliencyDetection_withoutMD);
                    }
                }
        );

        Saliency_withMD_button = (Button)findViewById(R.id.Saliency_withMD_button);
        Saliency_withMD_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startResultImageActivity(CommonImageProcessing.SaliencyDetection_withMD);
                    }
                }
        );

        TestAll_button = (Button)findViewById(R.id.testAll_button);
        TestAll_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startResultImageActivity(CommonImageProcessing.TestAll);
                    }
                }
        );

    }

    private void startResultImageActivity(int action){
        Intent intent = new Intent(ImageViewActivity.this, ResultImageActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("ACTION", action);
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
