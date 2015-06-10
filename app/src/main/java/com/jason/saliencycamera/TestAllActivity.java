package com.jason.saliencycamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import imageProcessing.CommonImageProcessing;
import imageProcessing.LSH;
import imageProcessing.SLIC;
import imageProcessing.SlicBuilder;
import imageProcessing.SuperpixelImage;


public class TestAllActivity extends Activity {

    private String[] drawable1Array;
    private String[] drawable2Array;
    private LinearLayout resultImageList;
    private TextView loadText;
    private TestAllActivity that;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_all);
        resultImageList = (LinearLayout)findViewById(R.id.resultImageList);
        loadText = (TextView)findViewById(R.id.loadText);
        that = this;

        // Initialize
        Bundle bundle = this.getIntent().getExtras();
        drawable1Array = bundle.getStringArray("drawable1Array");
        drawable2Array = bundle.getStringArray("drawable2Array");

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        for(int i=0;i<drawable1Array.length;i++) {
                            Log.d("TestAll", "\t\t--------Start of algorithm()----------");
                            String _drawable1 = drawable1Array[i];
                            String _drawable2 = drawable2Array[i];
                            Bitmap _bitmap1 = null;
                            Bitmap _bitmap2 = null;
                            Bitmap resultBitmap1;
                            Bitmap resultBitmap2;
                            Log.d("TestAll", "\t\tdrawable1=" + _drawable1);
                            Log.d("TestAll", "\t\tdrawable2=" + _drawable2);
                            Log.d("TestAll", "\t\tdrawable1Array=" + drawable1Array.toString());
                            Log.d("TestAll", "\t\ti=" + i);
                            Log.d("TestAll", "\t\tdrawable1Array[" + i + "]=" + drawable1Array[i]);
                            Log.d("TestAll", "\t\tdrawable1Array[" + i + "]=" + drawable2Array[i]);
                            try {
                                _bitmap1 = BitmapFactory.decodeStream(that.getAssets().open(_drawable1));
                                _bitmap2 = BitmapFactory.decodeStream(that.getAssets().open(_drawable2));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (_bitmap1 != null && _bitmap2 != null) {
                                Log.d("TestAll", "\t\t-------------------------Start of test case #" + i + " for " + _drawable2 + "-------------------------");
                                //Generate the IIF images
                                resultBitmap1 = LSH.IIF(_bitmap1);
                                resultBitmap2 = LSH.IIF(_bitmap2);
                                saveBitmap(_drawable1, CommonImageProcessing.LSHIIF, resultBitmap1);
                                saveBitmap(_drawable2, CommonImageProcessing.LSHIIF, resultBitmap2);

                                //Calculate the optical flow using the IIF images
                                MatOfPoint2f point1 = new MatOfPoint2f();
                                MatOfPoint2f point2 = new MatOfPoint2f();
                                MatOfByte resultStatus = new MatOfByte();
                                resultBitmap1 = CommonImageProcessing.motionDetection(resultBitmap1, resultBitmap2, resultBitmap1, point1, point2, resultStatus);
                                resultBitmap2 = resultBitmap1;
                                saveBitmap(_drawable2, CommonImageProcessing.MotionDetection, resultBitmap1);

                                //Work out the superpixel object for calculation
                                SLIC slic = new SlicBuilder().buildSLIC();
                                SuperpixelImage superpixel = slic.createSuperpixel(_bitmap1);

                                //Calculate the average displacement of each superpixel
                                superpixel.calculateDisplacement(point1, point2, resultStatus);
//                                        superpixel.setDisplacement(point1,point2, resultStatus);

                                //Get the difference between the two bitmap
                                resultBitmap1 = CommonImageProcessing.diffMap(_bitmap1, _bitmap2);
                                resultBitmap2 = resultBitmap1;
                                saveBitmap(_drawable2, CommonImageProcessing.DifferenceImage, resultBitmap1);

                                //Find the rough depth map
                                resultBitmap1 = CommonImageProcessing.roughDepthMap(_bitmap1, _bitmap2);
                                resultBitmap2 = resultBitmap1;
                                saveBitmap(_drawable2, CommonImageProcessing.SaliencyDetection_withoutMD, resultBitmap1);

                                //Use the superpixel object to calculate the roughDepthMap
                                resultBitmap1 = CommonImageProcessing.motionCompensatedSaliencyDetection(_bitmap1, _bitmap2, superpixel);
//                                        resultBitmap1 = CommonImageProcessing.motionCompensatedSaliencyDetection2(bitmap1, bitmap2, superpixel);
                                resultBitmap2 = resultBitmap1;
                                saveBitmap(_drawable2, CommonImageProcessing.SaliencyDetection_withMD, resultBitmap1);
                                addImage(resultBitmap1);
                                Log.d("TestAll", "\t\t-------------------------End of test case #" + i + " for " + _drawable2 + "-------------------------");

                                String oldText = loadText.getText().toString();
                                loadText.setText(oldText + "\ntest case #" + i + " for " + _drawable2 + " is loaded");
                            }
                        }
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        });

    }


    private void saveBitmap(String file_name, int action, Bitmap bitmap){
        if(bitmap!=null){
            FileOutputStream out = null;
            try {
                // Get file name
                String filename = "";
                filename = file_name;
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
                    case CommonImageProcessing.DifferenceImage:
                        filename+="_DifferenceImage";
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
                String timeStamp = new SimpleDateFormat("MM-dd").format(new Date());
                File todayFile = new File(mediaStorageDir, ""+timeStamp);
                // This location works best if you want the created images to be shared
                // between applications and persist after your app has been uninstalled.

                // Create the storage directory if it does not exist
                if (!todayFile.exists()) {
                    if (!todayFile.mkdirs()) {
                        Log.d("SaliencyCameraResult/" + timeStamp, "failed to create directory");
                    }
                }
                if (todayFile.exists()) {
                    out = new FileOutputStream(todayFile.getPath() + File.separator + filename+"_result"+".png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                }
                Log.d("TestAll",todayFile.getPath() + File.separator + filename+"_result"+".png is created");
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

    public void addImage(Bitmap bitmap){
        ImageView imgView = new ImageView(this);
        imgView.setImageBitmap(bitmap);
        resultImageList.addView(imgView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_all, menu);
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
