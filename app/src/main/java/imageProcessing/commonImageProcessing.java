package imageProcessing;

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
public class commonImageProcessing {
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
        Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY);

        Mat fg = new Mat(mat.size(),CvType.CV_8U);
        Imgproc.erode(threeChannel,fg,new Mat(),new Point(-1,-1),2);

        Mat bg = new Mat(mat.size(),CvType.CV_8U);
        Imgproc.dilate(threeChannel,bg,new Mat(),new Point(-1,-1),3);
        Imgproc.threshold(bg,bg,1, 128,Imgproc.THRESH_BINARY_INV);

        Mat markers = new Mat(mat.size(),CvType.CV_32SC1);
        Core.add(fg, bg, markers);

        WatershedSegmenter segmenter = new WatershedSegmenter();
//        if(markers == null){
//            Log.d("markers is Null? ","Yes");
//        }
//        else
//            Log.d("markers is Null? ","No");
        segmenter.setMarkers(markers);
        Mat resultMat = segmenter.process(mat);

        Bitmap result = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resultMat,result);
        return result;
    }
}
