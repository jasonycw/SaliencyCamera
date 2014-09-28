package com.jason.saliencycamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import imageProcessing.LSH;
import imageProcessing.commonImageProcessing;


public class ResultImageViewActivity extends Activity {
    private ImageView imageView;
    private ImageView resultImageView;
    private TextView textView;
    private Date oldDate;
    private Date newDate;

    private String picture1Uri = "";
    private String picture2Uri = "";
    private String drawable1;
    private String drawable2;

    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap subtractBitmap;
    private Bitmap divideBitmap;
    private Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_image_view);

        // Initialize
        Bundle bundle = this.getIntent().getExtras();
        picture1Uri = bundle.getString("picture1_URI");
        picture2Uri = bundle.getString("picture2_URI");
        oldDate = new Date(this.getIntent().getLongExtra("date", -1));

        imageView = (ImageView) findViewById(R.id.imageView);
        resultImageView = (ImageView) findViewById(R.id.resultImageView);
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
            Log.d("START_SUBTRACT", oldDate.toString());
//            subtractBitmap = commonImageProcessing.subtract(bitmap1, bitmap2);
//            divideBitmap = commonImageProcessing.divide(bitmap1, bitmap2);
//            resultBitmap = commonImageProcessing.multiply(subtractBitmap, divideBitmap);
            subtractBitmap = LSH.IIF(bitmap1);
            divideBitmap = LSH.IIF(bitmap2);
            resultBitmap = bitmap1;
            newDate = new Date();
            Log.d("END___SUBTRACT", newDate.toString());
            long timeDifference = newDate.getTime() - oldDate.getTime();
            imageView.setImageBitmap(subtractBitmap);
            resultImageView.setImageBitmap(resultBitmap);
            textView.setText("used " + timeDifference + " ms");
        }
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        imageView.setImageBitmap(subtractBitmap);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        imageView.setImageBitmap(divideBitmap);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        imageView.setImageBitmap(divideBitmap);
                        break;
                    }
                }
                return true;
            }
        };
        imageView.setOnTouchListener(onTouchListener);
        resultImageView.setOnTouchListener(onTouchListener);
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
