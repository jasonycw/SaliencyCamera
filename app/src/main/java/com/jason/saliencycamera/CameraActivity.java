package com.jason.saliencycamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CameraActivity extends Activity {
    public static final int MEDIA_TYPE_IMAGE = 1;
    Camera.Size pictureSize;
    private int counter;
    private String picture1Uri = "";
    private String picture2Uri = "";
    private Long timeDifference = Long.valueOf(0);
    private Date date = null;
    private ArrayList<byte[]> buffer = new ArrayList<byte[]>();
    private Camera mCamera = null;
    private Camera.Parameters params;
    private CameraPreview mPreview;

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            Log.d("Get Camera exception", e.toString());
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 1st onPictureTaken
            if (counter == 1) {
                // Save the 1 pic capture time
                date = new Date();
                Log.d("2) 1st Time", date.toString());

                // store the byte array data in buffer arraylist
                buffer.clear();
                buffer.add(data);

                // Turn on flash light and take the next picture
                mCamera.startPreview();
                params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(params);
                mCamera.takePicture(null, null, mPicture);
                Log.d("3) TAKE PICTURE", "2nd picture captured");
//                logShowParameterDetails(camera);
            }
            // 2nd onPicture Taken
            else {
                // Calculate time difference
                Long oldDate = date.getTime();
                date = new Date();
                Long newDate = date.getTime();
                timeDifference = newDate - oldDate;
                Log.d("4) 2st Time", newDate.toString());
                Log.d("5) Time Difference", timeDifference + "ms");

                // store the byte array data in buffer arraylist
                buffer.add(data);

                // Save the file
                for (int i = 0; i < buffer.size(); i++)
                    saveFile(buffer.get(i), i + 1);
                Log.d("6) FILE SAVED", buffer.size() + " file(s) in buffer are saved");

                // Reset camera
                params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(params);
                mCamera.startPreview();
                Log.d("7) CAMERA", "Camera reset.");

                // Pass data to next activity
                Intent intent = new Intent(CameraActivity.this, ImageViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("picture1_URI", picture1Uri);
                bundle.putString("picture2_URI", picture2Uri);
                bundle.putLong("timeDifference", timeDifference);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            counter--;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final Context context = getApplicationContext();

        // Create an instance of Camera
        if (mCamera == null) {
            mCamera = this.getCameraInstance();
            Log.d("Get Camera Instance", (mCamera == null) ? "Fail" : "Success");
        }
        // get Camera parameters
        params = mCamera.getParameters();
        // set the focus mode
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        // set resolution
        pictureSize = getSmallestPictureSize(params);
        params.setPictureSize(pictureSize.width, pictureSize.height);
        // set Camera parameters
        mCamera.setParameters(params);

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        counter = 1;
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (success) {
                                    // get an image from the camera
                                    camera.takePicture(null, null, mPicture);
                                    Log.d("1) TAKE PICTURE", "1st picture captured");
//                                    logShowParameterDetails(camera);
                                } else {
                                    Log.d("1) TAKE PICTURE", "CANNOT FOCUS...");
                                    logShowParameterDetails(camera);

                                    CharSequence text = "Cannot focus";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        });
                    }
                }
        );

        // Add a listener to the Capture button
        Button testButton = (Button) findViewById(R.id.button_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass data to next activity
                Intent intent = new Intent(CameraActivity.this, TestImageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume: ", (mCamera == null) ? "mCamera == null " : "mCamera != null ");
        if (mCamera == null) {
            mCamera = this.getCameraInstance();
            Log.d("onResume : Get Camera Instance", (mCamera == null) ? "Fail" : "Success");
        }

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();
        preview.addView(mPreview);

        params = mCamera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        params.setPictureSize(pictureSize.width, pictureSize.height);
        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.releaseCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        buffer.clear();
//        releaseCamera();              // release the camera immediately on pause event
    }

    /**
     * Release Camera object
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea < resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }

    /**
     * Create a File for saving an image
     */
    private File getOutputMediaFile(int type, int count) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS ").format(date);
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + "(" + count + ").jpg");
            if (count == 1)
                picture1Uri = mediaStorageDir.getPath() + File.separator + timeStamp + "(" + count + ").jpg";
            else if (count == 2)
                picture2Uri = mediaStorageDir.getPath() + File.separator + timeStamp + "(" + count + ").jpg";
            Log.d("SAVE AT", "picture" + count + "Uri:\t" + mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void logShowParameterDetails(Camera camera) {
        Log.d("Parameter \tWhite Balance", camera.getParameters().getWhiteBalance());
        Log.d("Parameter \tExposure Compensation", String.valueOf(camera.getParameters().getExposureCompensation()));
        Log.d("Parameter \tFocal Length", String.valueOf(camera.getParameters().getFocalLength()));
        Log.d("Parameter \tFocus Mode", String.valueOf(camera.getParameters().getFocusMode()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
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

    // Method for saving the file from byte array
    private void saveFile(byte[] data, int count) {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, count);
        if (pictureFile == null) {
            Log.d(LAYOUT_INFLATER_SERVICE, "Error creating media file, check storage permissions. ");
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(LAYOUT_INFLATER_SERVICE, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(LAYOUT_INFLATER_SERVICE, "Error accessing file: " + e.getMessage());
        }
    }


}
