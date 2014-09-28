package com.jason.saliencycamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;


public class TestImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_image);

        ImageView lenaImage = (ImageView)findViewById(R.id.lenaImage);
        try {
            lenaImage.setImageBitmap(BitmapFactory.decodeStream(getApplicationContext().getAssets().open("lena1_.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lenaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass data to next activity
                Intent intent = new Intent(TestImageActivity.this, ImageViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("drawable1", "lena2_.png");
                bundle.putString("drawable2", "lena1_.png");
                bundle.putLong("timeDifference", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageView toyImage = (ImageView)findViewById(R.id.toyImage);
        try {
            toyImage.setImageBitmap(BitmapFactory.decodeStream(getApplicationContext().getAssets().open("toy.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        toyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass data to next activity
                Intent intent = new Intent(TestImageActivity.this, ImageViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("drawable1", "toy.jpg");
                bundle.putString("drawable2", "toy_lighted.jpg");
                bundle.putLong("timeDifference", 0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_image, menu);
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
}
