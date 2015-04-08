package com.jason.saliencycamera;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by Jason on 13/11/2014.
 */
public class ImageAdapter extends BaseAdapter {
    // references to our images
    public static String[] NO_FLASH_IMAGE_NAMES = {
            "background_movement1.jpg",
            "background_movement1.jpg",
            "background_movement2.jpg",
            "background_movement2.jpg",
            "background_movement3.jpg",
            "background_movement3.jpg",
            "both_movement1.jpg",
            "both_movement1.jpg",
            "both_movement1.jpg",
            "both_movement1.jpg",
            "both_movement1.jpg",
            "both_movement1.jpg",
            "both_movement2.jpg",
            "both_movement2.jpg",
            "both_movement2.jpg",
            "both_movement2.jpg",
            "both_movement2.jpg",
            "both_movement3.jpg",
            "both_movement3.jpg",
            "both_movement3.jpg",
            "both_movement3.jpg",
            "both_movement3.jpg",
            "both_movement3.jpg",
            "both_movement3.jpg",
            "camera_movement1.jpg",
            "camera_movement1.jpg",
            "camera_movement1.jpg",
            "camera_movement1.jpg",
            "camera_movement1.jpg",
            "camera_movement1.jpg",
            "camera_movement1.jpg",
            "camera_movement1.jpg",
            "camera_movement2.jpg",
            "camera_movement2.jpg",
            "camera_movement2.jpg",
            "camera_movement2.jpg",
            "camera_movement2.jpg",
            "camera_movement2.jpg",
            "camera_movement3.jpg",
            "camera_movement3.jpg",
            "camera_movement3.jpg",
            "camera_movement3.jpg",
            "camera_movement3.jpg",
            "object_change1.jpg",
            "object_change1.jpg",
            "object_change1.jpg",
            "object_change1.jpg",
            "object_change2.jpg",
            "object_change2.jpg",
            "object_change2.jpg",
            "object_change2.jpg",
            "object_change2.jpg",
            "object_change2.jpg",
            "object_movement1.jpg",
            "object_movement1.jpg",
            "object_movement1.jpg",
            "object_movement1.jpg",
            "object_movement1.jpg",
            "object_movement1.jpg",
            "object_movement1.jpg",
            "object_movement1.jpg",
            "object_movement2.jpg",
            "object_movement2.jpg",
            "object_movement2.jpg",
            "object_movement2.jpg",
            "object_movement2.jpg",
            "object_movement2.jpg",
            "object_movement3.jpg",
            "object_movement3.jpg",
            "object_movement3.jpg",
            "object_movement3.jpg",
            "object_movement3.jpg",
            "object_movement3.jpg",
            "object_movement3.jpg"
//            "tennis.jpg",
//            "tennis2.jpg",
//            "building.jpg",
//            "micky.jpg",
//            "bag.jpg",
//            "phone2.jpg",
//            "phone3.jpg",
//            "chicken.jpg",
//            "toy.jpg",
//            "3dprinting.jpg",
//            "lena2_.png"
    };
    public static final String[] FLASH_IMAGE_NAMES = {
            "background_movement1_light1.jpg",
            "background_movement1_light2.jpg",
            "background_movement2_light1.jpg",
            "background_movement2_light2.jpg",
            "background_movement3_light1.jpg",
            "background_movement3_light2.jpg",
            "both_movement1_light1.jpg",
            "both_movement1_light2.jpg",
            "both_movement1_light3.jpg",
            "both_movement1_light4.jpg",
            "both_movement1_light5.jpg",
            "both_movement1_light6.jpg",
            "both_movement2_light1.jpg",
            "both_movement2_light2.jpg",
            "both_movement2_light3.jpg",
            "both_movement2_light4.jpg",
            "both_movement2_light5.jpg",
            "both_movement3_light1.jpg",
            "both_movement3_light2.jpg",
            "both_movement3_light3.jpg",
            "both_movement3_light4.jpg",
            "both_movement3_light5.jpg",
            "both_movement3_light6.jpg",
            "both_movement3_light7.jpg",
            "camera_movement1_light1.jpg",
            "camera_movement1_light2.jpg",
            "camera_movement1_light3.jpg",
            "camera_movement1_light4.jpg",
            "camera_movement1_light5.jpg",
            "camera_movement1_light6.jpg",
            "camera_movement1_light7.jpg",
            "camera_movement1_light8.jpg",
            "camera_movement2_light1.jpg",
            "camera_movement2_light2.jpg",
            "camera_movement2_light3.jpg",
            "camera_movement2_light4.jpg",
            "camera_movement2_light5.jpg",
            "camera_movement2_light6.jpg",
            "camera_movement3_light1.jpg",
            "camera_movement3_light2.jpg",
            "camera_movement3_light3.jpg",
            "camera_movement3_light4.jpg",
            "camera_movement3_light5.jpg",
            "object_change1_light1.jpg",
            "object_change1_light2.jpg",
            "object_change1_light3.jpg",
            "object_change1_light4.jpg",
            "object_change2_light1.jpg",
            "object_change2_light2.jpg",
            "object_change2_light3.jpg",
            "object_change2_light4.jpg",
            "object_change2_light5.jpg",
            "object_change2_light6.jpg",
            "object_movement1_light1.jpg",
            "object_movement1_light2.jpg",
            "object_movement1_light3.jpg",
            "object_movement1_light4.jpg",
            "object_movement1_light5.jpg",
            "object_movement1_light6.jpg",
            "object_movement1_light7.jpg",
            "object_movement1_light8.jpg",
            "object_movement2_light1.jpg",
            "object_movement2_light2.jpg",
            "object_movement2_light3.jpg",
            "object_movement2_light4.jpg",
            "object_movement2_light5.jpg",
            "object_movement2_light6.jpg",
            "object_movement3_light1.jpg",
            "object_movement3_light2.jpg",
            "object_movement3_light3.jpg",
            "object_movement3_light4.jpg",
            "object_movement3_light5.jpg",
            "object_movement3_light6.jpg",
            "object_movement3_light7.jpg"
//            "tennis_lighted.jpg",
//            "tennis2_lighted.jpg",
//            "building_lighted.jpg",
//            "micky_lighted.jpg",
//            "bag_lighted.jpg",
//            "phone2_lighted.jpg",
//            "phone3_lighted.jpg",
//            "chicken_lighted.jpg",
//            "toy_lighted.jpg",
//            "3dprinting_lighted.jpg",
//            "lena1_.png"
    };

    private Context mContext;
    private GridView mGridView;

    public ImageAdapter(Context c, GridView g) {
        mContext = c;
        mGridView = g;
    }

    public int getCount() {
        return NO_FLASH_IMAGE_NAMES.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, (int) (mGridView.getHeight()/2.5)));
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, 250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

//        imageView.setImageResource(NO_FLASH_IMAGE_NAMES[position]);
        try {
//            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(NO_FLASH_IMAGE_NAMES[position])));
            imageView.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open(FLASH_IMAGE_NAMES[position])));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageView;
    }


}
