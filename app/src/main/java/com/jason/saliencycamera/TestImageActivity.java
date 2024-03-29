package com.jason.saliencycamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Date;


public class TestImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_image);

        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(this,gridview));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Toast.makeText(HelloGridView.this, "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TestImageActivity.this, ImageViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("drawable1", ImageAdapter.NO_FLASH_IMAGE_NAMES[position]);
                bundle.putString("drawable2", ImageAdapter.FLASH_IMAGE_NAMES[position]);
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
        if (id == R.id.action_testall) {
            Intent intent = new Intent(TestImageActivity.this, TestAllActivity.class);

            Bundle bundle = new Bundle();
            bundle.putStringArray("drawable1Array", ImageAdapter.NO_FLASH_IMAGE_NAMES);
            bundle.putStringArray("drawable2Array", ImageAdapter.FLASH_IMAGE_NAMES);

            intent.putExtras(bundle);
            intent.putExtra("date", new Date().getTime());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startResultImageActivity(int action, String drawable1, String drawable2){
        Intent intent = new Intent(TestImageActivity.this, ResultImageActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("ACTION", action);
        bundle.putString("drawable1", drawable1);
        bundle.putString("drawable2", drawable2);

        intent.putExtras(bundle);
        intent.putExtra("date", new Date().getTime());
        startActivity(intent);
    }
}
