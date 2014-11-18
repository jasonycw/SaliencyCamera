package imageProcessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jason on 7/9/2014.
 */
public class CommonImageProcessing {
    public static final int LSHIIF = 2;
    public static final int MotionDetection = 4;
    public static final int SLIC = 8;
    public static final int SaliencyDetection_withoutMD = 16;
    public static final int SaliencyDetection_withMD = 32;


    public static Bitmap toGrayScale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static float getValue(Bitmap bitmap, int x, int y) {
        if(x<0||y<0||x>(bitmap.getWidth()-1)||y>(bitmap.getHeight()-1))
            Log.d("GET PIXEL ERROR!!!!!!!!!!!!", "X,Y is " + x + "," + y+ "\tmax X,Y is" + bitmap.getWidth()+","+bitmap.getHeight());
        int color = bitmap.getPixel(x, y);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        float intensity = (float) (0.299 * red + 0.587 * green + 0.114 * blue);
        return intensity;
    }

    public static String getARGBstring(Bitmap bitmap, int x, int y){
        return "ARGB at "+x+","+y+" is \t\tA " + String.format("%03d", Color.alpha(bitmap.getPixel(x,y))) +"\t\tR "+ String.format("%03d", Color.red(bitmap.getPixel(x,y))) +"\t\tG "+ String.format("%03d",  Color.green(bitmap .getPixel(x,y))) +"\t\tB "+ String.format("%03d",  Color.blue(bitmap.getPixel(x,y)));
    }

    public static Bitmap subtract(Bitmap bitmap1, Bitmap bitmap2) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        Bitmap grayBitmap1 = toGrayScale(bitmap1);
        Bitmap grayBitmap2 = toGrayScale(bitmap2);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int value1 = Color.blue(grayBitmap1.getPixel(x, y));
                int value2 = Color.blue(grayBitmap2.getPixel(x, y));
                int resultValue = Math.abs(value2 - value1);
//                resultValue = (resultValue>255/3)?resultValue:0;
                result.setPixel(x, y, Color.rgb(resultValue, resultValue, resultValue));
            }
//        grayBitmap1.recycle();
//        grayBitmap2.recycle();
        return result;
    }

    public static Bitmap multiply(Bitmap bitmap1, Bitmap bitmap2) {
        double alpha = 0.1;
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        Bitmap grayBitmap1 = toGrayScale(bitmap1);
        Bitmap grayBitmap2 = toGrayScale(bitmap2);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int value1 = Color.blue(grayBitmap1.getPixel(x, y));
                int value2 = Color.blue(grayBitmap2.getPixel(x, y));
                int resultValue = (int) (Math.abs((value2/255.0) * (value1/255.0))*255);
//                resultValue = (resultValue>255/2)?255:0;
                result.setPixel(x, y, Color.rgb(resultValue, resultValue, resultValue));
            }
//        grayBitmap1.recycle();
//        grayBitmap2.recycle();
        return result;
    }

    public static Bitmap divide(Bitmap bitmap1, Bitmap bitmap2) {
        double alpha = Double.MIN_VALUE;
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        Bitmap grayBitmap1 = toGrayScale(bitmap1);
        Bitmap grayBitmap2 = toGrayScale(bitmap2);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int value1 = Color.blue(grayBitmap1.getPixel(x, y));
                int value2 = Color.blue(grayBitmap2.getPixel(x, y));
//                int resultValue = (int) (Math.abs((Math.log((value1 + alpha)/(value2 + alpha))) * 1000));
                int resultValue = (int) ((Math.abs(Math.log((value1 + alpha)/(value2 + alpha))))*255);
                result.setPixel(x, y, Color.rgb(resultValue, resultValue, resultValue));
            }
//        grayBitmap1.recycle();
//        grayBitmap2.recycle();
        return result;
    }

    public static Bitmap imageSegmentation(Bitmap input){
        int width = input.getWidth();
        int height = input.getHeight();

        Mat mat = new Mat(height,width, CvType.CV_8UC3);
        Utils.bitmapToMat(input,mat);
        Imgproc.cvtColor(mat,mat, Imgproc.COLOR_RGBA2BGR);
//        Log.d("Mat type: ",Integer.toString(mat.type()));
//        Log.d("Mat channels: ",Integer.toString(mat.channels()));
//        Log.d("Mat depth: ",Integer.toString(mat .depth()));

//        Mat resultMat = new Mat(height,width, CvType.CV_32SC1);
//        Log.d("resultMat type: ",Integer.toString(resultMat.type()));
//        Log.d("resultMat channels: ",Integer.toString(resultMat.channels()));
//        Log.d("resultMat depth: ",Integer.toString(resultMat.depth()));
//
//        Imgproc.watershed(mat, resultMat);
//        Log.d("resultMat type (after watershed): ",Integer.toString(resultMat.type()));
//        Log.d("resultMat channels (after watershed): ",Integer.toString(resultMat.channels()));
//        Log.d("resultMat depth (after watershed): ",Integer.toString(resultMat.depth()));
//
//        resultMat.convertTo(resultMat,CvType.CV_8UC3);
//        Log.d("resultMat type (after convertTo) : ",Integer.toString(resultMat.type()));
//        Log.d("resultMat channels (after convertTo): ",Integer.toString(resultMat.channels()));
//        Log.d("resultMat depth (after convertTo): ",Integer.toString(resultMat.depth()));

        Mat threeChannel = new Mat();
        Imgproc.cvtColor(mat, threeChannel, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY); //result in 2-bit image (black < 3rd Parameter < white)
//        Mat threshold85 = new Mat();
//        Mat threshold170 = new Mat();
//        Imgproc.cvtColor(mat, threshold85, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.threshold(threshold85 , threshold85 , 85, 85, Imgproc.THRESH_BINARY); //result in 2-bit image (black < 3rd Parameter < white)
//        Imgproc.cvtColor(mat, threshold170, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.threshold(threshold170, threshold170 , 170, 170, Imgproc.THRESH_BINARY); //result in 2-bit image (black < 3rd Parameter < white)

//        Mat fg = new Mat(mat.size(),CvType.CV_8U);
//        Imgproc.erode(threeChannel,fg,new Mat(),new Point(-1,-1),3);//result in a "bold" image (objects are larger than it should)
//
//        Mat bg = new Mat(mat.size(),CvType.CV_8U);
//        Imgproc.dilate(threeChannel,bg,new Mat(),new Point(-1,-1),3);//result in a "slim" image (objects are smaller than it should)
//        Imgproc.threshold(bg,bg,1, 128,Imgproc.THRESH_BINARY_INV);
//
//        Mat markers = new Mat(mat.size(),CvType.CV_32SC1);
//        Core.add(fg, bg, markers);

        Mat markers = createMarkers(threeChannel,255);
//        Mat markers = new Mat(mat.size(),CvType.CV_32SC1);
//        Core.add(createMarkers(threshold85,85) , createMarkers(threshold170,170), markers);

        WatershedSegmenter segmenter = new WatershedSegmenter();
        segmenter.setMarkers(markers);
        Mat resultMat = segmenter.process(mat);

        Bitmap result = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(threeChannel,result);
//        Utils.matToBitmap(fg,result);
//        Utils.matToBitmap(bg,result);
//        Utils.matToBitmap(markers,result);
        Utils.matToBitmap(resultMat,result);
        return result;
    }

    public static Mat createMarkers(Mat thresholdedImage, int thresh){
        Mat fg = new Mat(thresholdedImage.size(),CvType.CV_8U);
        Imgproc.erode(thresholdedImage,fg,new Mat(),new Point(-1,-1),3);//result in a "bold" image (objects are larger than it should)

        Mat bg = new Mat(thresholdedImage.size(),CvType.CV_8U);
        Imgproc.dilate(thresholdedImage,bg,new Mat(),new Point(-1,-1),3);//result in a "slim" image (objects are smaller than it should)
        Imgproc.threshold(bg,bg,1, thresh/2,Imgproc.THRESH_BINARY_INV);

        Mat markers = new Mat(thresholdedImage.size(),CvType.CV_32SC1);
        Core.add(fg, bg, markers); //Create outline for the input threshold image

        return markers;
    }

    public static Bitmap SLIC(Bitmap input){
        SLIC slic = new SlicBuilder().buildSLIC();
        return slic.createBoundedBitmap(input);
    }

    public static Bitmap SLIC(Bitmap input, Context context){
        SLIC slic = new SlicBuilder().context(context).buildSLIC();
        return slic.createBoundedBitmap(input);
    }

    public static Bitmap roughDepthMap(Bitmap non_flash_image_bitmap, Bitmap flash_image_bitmap) {
        int width = non_flash_image_bitmap.getWidth();
        int height = non_flash_image_bitmap.getHeight();

        Mat no_flash_mat = new Mat(height,width, CvType.CV_8UC3);
        Utils.bitmapToMat(non_flash_image_bitmap,no_flash_mat);
        Imgproc.cvtColor(no_flash_mat,no_flash_mat,Imgproc.COLOR_BGR2GRAY);

        Mat flash_mat = new Mat(height, width, CvType.CV_8UC3);
        Utils.bitmapToMat(flash_image_bitmap,flash_mat);
        Imgproc.cvtColor(flash_mat,flash_mat,Imgproc.COLOR_BGR2GRAY);

        Mat diff_mat = new Mat();
        Core.subtract(flash_mat,no_flash_mat,diff_mat);

        Mat ratio_mat = new Mat();
        Mat temp_mat = new Mat();
        Core.divide(flash_mat, no_flash_mat, temp_mat);
        temp_mat.convertTo(temp_mat,CvType.CV_32F);
        Core.log(temp_mat,ratio_mat);

        ratio_mat.convertTo(ratio_mat,CvType.CV_8UC3);
        Mat result_mat = diff_mat.mul(ratio_mat);

        Bitmap result_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Utils.matToBitmap(result_mat,result_bitmap);

        return result_bitmap;
    }
}
