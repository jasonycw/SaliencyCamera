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
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 7/9/2014.
 */
public class CommonImageProcessing {
    public static final int LSHIIF = 2;
    public static final int MotionDetection = 4;
    public static final int SLIC = 8;
    public static final int SaliencyDetection_withoutMD = 16;
    public static final int SaliencyDetection_withMD = 32;
    public static final int PIXEL_DISPLACEMENT_THRESHOLD = 10;


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
        SuperpixelImage superpixel = slic.createSuperpixel(input);
        return superpixel.createBitmapWithBoundary(input);
    }

    public static Bitmap diffMap(Bitmap non_flash_image_bitmap, Bitmap flash_image_bitmap) {
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

        SLIC slic = new SlicBuilder().buildSLIC();
        SuperpixelImage superpixel = slic.createSuperpixel(non_flash_image_bitmap);

        return superpixel.calculateValue(diff_mat).getValueBitmap();
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

        SLIC slic = new SlicBuilder().buildSLIC();
        SuperpixelImage superpixel = slic.createSuperpixel(non_flash_image_bitmap);

        return superpixel.calculateContrastValueMat(diff_mat).getContrastValueBitmap();
    }

    public static Bitmap motionDetection(Bitmap non_flash_image_bitmap, Bitmap flash_image_bitmap, Bitmap resultBackgroud){
        Mat result_mat = new Mat(non_flash_image_bitmap.getHeight(), non_flash_image_bitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(resultBackgroud,result_mat);

        MatOfPoint2f point1 = new MatOfPoint2f();
        MatOfPoint2f point2 = new MatOfPoint2f();
        MatOfByte status = new MatOfByte();
        opticalFlow(non_flash_image_bitmap, flash_image_bitmap, point1, point2, status);
//        perPixelOpticalFlow(non_flash_image_bitmap, flash_image_bitmap, point1, point2, status);

        List<Point> cornersPrev = new ArrayList<Point>();
        cornersPrev = point1.toList();

        List<Point> cornersThis = new ArrayList<Point>();
        cornersThis = point2.toList();

        List<Byte> opticalFlowResultStatus = new ArrayList<Byte>();
        opticalFlowResultStatus = status.toList();

        int y = opticalFlowResultStatus.size() - 1;

        Point pt, pt2;
        Scalar colorRed = new Scalar(255, 0, 0, 255);
        Scalar colorGreen = new Scalar(0, 255, 0, 255);
        Scalar colorBlue = new Scalar(0, 0, 255, 100);
        int iLineThickness = 1;

        for (int x = 0; x < y; x++) {
            if (opticalFlowResultStatus.get(x) == 1) {
                pt = cornersThis.get(x);
                pt2 = cornersPrev.get(x);

//                Core.circle(result_mat, pt, 5, colorRed, iLineThickness - 1);
                if(Math.sqrt(Math.pow(Math.abs(pt.x-pt2.x),2) + Math.pow(Math.abs(pt.y-pt2.y),2)) <= CommonImageProcessing.PIXEL_DISPLACEMENT_THRESHOLD)
                    Core.line(result_mat, pt, pt2, colorRed, iLineThickness);
                else
                    Core.line(result_mat, pt, pt2, colorBlue, iLineThickness);
            }
            else {
                pt = cornersThis.get(x);
                Core.line(result_mat, pt, pt, colorGreen, iLineThickness);
            }
        }

        // Change the value matrix to Bitmap
        Bitmap result_bitmap = Bitmap.createBitmap(result_mat.width(), result_mat.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(result_mat,result_bitmap);
        return result_bitmap;
    }

    public static void opticalFlow(Bitmap bitmap1, Bitmap bitmap2, MatOfPoint2f pointInBitmap1, MatOfPoint2f pointInBitmap2, MatOfByte status){
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();

        Mat mat1 = new Mat(height,width, CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap1,mat1);

        Mat mat2 = new Mat(height, width, CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap2,mat2);

        Imgproc.cvtColor(mat1,mat1,Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2,mat2,Imgproc.COLOR_BGR2GRAY);

        // Find MAX_NUM_OF_SAMPLE_POINT of point as sample point
        MatOfPoint samplePoints = new MatOfPoint();
        int MAX_NUM_OF_SAMPLE_POINT = 100000000;
        Imgproc.goodFeaturesToTrack(mat1, samplePoints, MAX_NUM_OF_SAMPLE_POINT, 0.0001, 1);

        pointInBitmap1.fromArray(samplePoints.toArray());
        pointInBitmap2.fromArray(samplePoints.toArray());

        MatOfFloat error = new MatOfFloat();

         /*
            Parameters:
                mat1 first 8-bit input image
                mat2 second input image
                pointInBitmap1 vector of 2D points for which the flow needs to be found; point coordinates must be single-precision floating-point numbers.
                pointInBitmap2 output vector of 2D points (with single-precision floating-point coordinates) containing the calculated new positions of input features in the second image; when OPTFLOW_USE_INITIAL_FLOW flag is passed, the vector must have the same size as in the input.
                status output status vector (of unsigned chars); each element of the vector is set to 1 if the flow for the corresponding features has been found, otherwise, it is set to 0.
                error output vector of errors; each element of the vector is set to an error for the corresponding feature, type of the error measure can be set in flags parameter; if the flow wasn't found then the error is not defined (use the status parameter to find such cases).
        */
        Video.calcOpticalFlowPyrLK(mat1, mat2, pointInBitmap1, pointInBitmap2, status, error);
    }

    public static void perPixelOpticalFlow(Bitmap bitmap1, Bitmap bitmap2, MatOfPoint2f pointInBitmap1, MatOfPoint2f pointInBitmap2, MatOfByte status){
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();

        Mat mat1 = new Mat(height,width, CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap1,mat1);

        Mat mat2 = new Mat(height, width, CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap2,mat2);

        Imgproc.cvtColor(mat1,mat1,Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2,mat2,Imgproc.COLOR_BGR2GRAY);

        // Create a Point object for each pixel
        ArrayList<Point> points = new ArrayList<Point>();
        for(int w=0;w<width;w++){
            for(int h=0;h<height;h++){
                points.add(new Point(w,h));
            }
        }
        pointInBitmap1.fromList(points);
        pointInBitmap2.fromList(points);

        MatOfFloat error = new MatOfFloat();
         /*
            Parameters:
                mat1 first 8-bit input image
                mat2 second input image
                pointInBitmap1 vector of 2D points for which the flow needs to be found; point coordinates must be single-precision floating-point numbers.
                pointInBitmap2 output vector of 2D points (with single-precision floating-point coordinates) containing the calculated new positions of input features in the second image; when OPTFLOW_USE_INITIAL_FLOW flag is passed, the vector must have the same size as in the input.
                status output status vector (of unsigned chars); each element of the vector is set to 1 if the flow for the corresponding features has been found, otherwise, it is set to 0.
                error output vector of errors; each element of the vector is set to an error for the corresponding feature, type of the error measure can be set in flags parameter; if the flow wasn't found then the error is not defined (use the status parameter to find such cases).
        */
        Video.calcOpticalFlowPyrLK(mat1, mat2, pointInBitmap1, pointInBitmap2, status, error);
    }

    public static Bitmap motionCompensatedSaliencyDetection(Bitmap non_flash_image_bitmap, Bitmap flash_image_bitmap, SuperpixelImage superPixel) {
        int width = non_flash_image_bitmap.getWidth();
        int height = non_flash_image_bitmap.getHeight();

        Mat no_flash_mat = new Mat(height,width, CvType.CV_8UC3);
        Utils.bitmapToMat(non_flash_image_bitmap,no_flash_mat);
        Imgproc.cvtColor(no_flash_mat,no_flash_mat,Imgproc.COLOR_BGR2GRAY);

        Mat flash_mat = new Mat(height, width, CvType.CV_8UC3);
        Utils.bitmapToMat(flash_image_bitmap,flash_mat);
        Imgproc.cvtColor(flash_mat,flash_mat,Imgproc.COLOR_BGR2GRAY);

        Mat diff_mat = new Mat(height,width, CvType.CV_8UC1);

        //Use superpixel displacement value to do the subtraction
        for (int row_y = 0; row_y < no_flash_mat.rows(); row_y++){
            for (int column_x = 0; column_x < no_flash_mat.cols(); column_x++){
                double no_flash_value = no_flash_mat.get(row_y, column_x)[0];
                Point displacement_offset = superPixel.getDisplacement(column_x, row_y);
                double flash_value;
                if(row_y+displacement_offset.y>=0 && column_x+displacement_offset.x>=0 &&
                    row_y+displacement_offset.y<no_flash_mat.rows() && column_x+displacement_offset.x<no_flash_mat.rows())
                    flash_value = flash_mat.get((int)(row_y+displacement_offset.y),(int)(column_x+displacement_offset.x))[0];
                else
                    flash_value = no_flash_mat.get(row_y, column_x)[0];
                diff_mat.put(row_y,column_x,flash_value-no_flash_value);
            }
        }

        return superPixel.calculateContrastValueMat(diff_mat).getContrastValueBitmap();
    }

    public static Bitmap motionCompensatedSaliencyDetection2(Bitmap non_flash_image_bitmap, Bitmap flash_image_bitmap, SuperpixelImage superPixel) {
        int width = non_flash_image_bitmap.getWidth();
        int height = non_flash_image_bitmap.getHeight();

        Mat no_flash_mat = new Mat(height,width, CvType.CV_8UC3);
        Utils.bitmapToMat(non_flash_image_bitmap,no_flash_mat);
        Imgproc.cvtColor(no_flash_mat,no_flash_mat,Imgproc.COLOR_BGR2GRAY);

        Mat flash_mat = new Mat(height, width, CvType.CV_8UC3);
        Utils.bitmapToMat(flash_image_bitmap,flash_mat);
        Imgproc.cvtColor(flash_mat,flash_mat,Imgproc.COLOR_BGR2GRAY);

        Mat diff_mat = new Mat(height,width, CvType.CV_8UC1);

        //Use displacement value to do the subtraction
        for (int row_y = 0; row_y < no_flash_mat.rows(); row_y++){
            for (int column_x = 0; column_x < no_flash_mat.cols(); column_x++){
                double no_flash_value = no_flash_mat.get(row_y, column_x)[0];
                Point displacement_offset = superPixel.getPixelDisplacement(column_x, row_y);
                double flash_value;
                if(row_y+displacement_offset.y>=0 && column_x+displacement_offset.x>=0 &&
                        row_y+displacement_offset.y<no_flash_mat.rows() && column_x+displacement_offset.x<no_flash_mat.rows() &&
                        (displacement_offset.x!=999 && displacement_offset.y!=999)){
                    flash_value = flash_mat.get((int)(row_y+displacement_offset.y),(int)(column_x+displacement_offset.x))[0];
                }
                else
                    flash_value = no_flash_mat.get(row_y, column_x)[0];
                diff_mat.put(row_y,column_x,flash_value-no_flash_value);
            }
        }

        return superPixel.calculateContrastValueMat(diff_mat).getContrastValueBitmap();
    }
}
