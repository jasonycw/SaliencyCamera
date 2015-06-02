package imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.MatOfByte;
import org.opencv.core.Point;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import tools.DebugTools;
import tools.MathHelper;
import tools.VectorComparator;

/**
 * Created by Jason on 15/12/2014.
 */
public class SuperpixelImage {
    Mat sobel;
    private MatOfPoint2f centersOfSuperpixels = null;
    private MatOfPoint2f displacementOfSuperpixels = null;
    private MatOfPoint2f displacementOfEachPixels = null;
    private MatOfDouble valueOfEachSuperpixel = null;
    private MatOfDouble contrastValueOfEachSuperpixel = null;
    private MatOfInt listOfSuperpixelsID = null;
    private MatOfInt pixelCountForEachSuperpixel = null;
    private Mat superpixelsID = null;

    public SuperpixelImage(){
        sobel = new Mat(3, 3, CvType.CV_32FC1);
        sobel.put(0, 0, -1/16.);
        sobel.put(0, 1, -2/16.);
        sobel.put(0, 2, -1/16.);
        sobel.put(1, 0, 0);
        sobel.put(1, 1, 0);
        sobel.put(1, 2, 0);
        sobel.put(2, 0, 1/16.);
        sobel.put(2, 1, 2/16.);
        sobel.put(2, 2, 1/16.);
    }

    public SuperpixelImage(MatOfPoint2f centersOfSuperpixels, MatOfInt listOfSuperpixelsID, Mat superpixelsID, Mat sobel){
        this.centersOfSuperpixels = centersOfSuperpixels;
        this.listOfSuperpixelsID = listOfSuperpixelsID;
        this.superpixelsID = superpixelsID;
        this.sobel = sobel;
    }

    public MatOfPoint2f getCentersOfSuperpixels() {
        return centersOfSuperpixels;
    }

    public void setCentersOfSuperpixels(MatOfPoint2f centersOfSuperpixels) {
        this.centersOfSuperpixels = centersOfSuperpixels;
    }

    public MatOfDouble getValueOfEachSuperpixel() {
        return valueOfEachSuperpixel;
    }

    public void setValueOfEachSuperpixel(MatOfDouble valueOfEachSuperpixel) {
        this.valueOfEachSuperpixel = valueOfEachSuperpixel;
    }

    public MatOfInt getListOfSuperpixelsID() {
        return listOfSuperpixelsID;
    }

    public void setListOfSuperpixelsID(MatOfInt listOfSuperpixelsID) {
        this.listOfSuperpixelsID = listOfSuperpixelsID;
    }

    public Mat getSuperpixelsID() {
        return superpixelsID;
    }

    public void setSuperpixelsID(Mat superpixelsID) {
        this.superpixelsID = superpixelsID;
    }

    public SuperpixelImage calculatePixelCountForEachSuperpixel(){
        List<Integer> superpixelIDList = listOfSuperpixelsID.toList();
        List<Integer> totalPixelsList = new ArrayList<Integer>();

        for(int i = 0; i < superpixelIDList.size(); i++) {
            // Calculate the number of pixels in this superpixel
            int id = superpixelIDList.get(i);
            Mat booleanMat = new Mat();
            Core.compare(superpixelsID, new Scalar(id), booleanMat, Core.CMP_EQ);
            Core.divide(booleanMat, new Scalar(255), booleanMat);
            int totalPixels = Core.countNonZero(booleanMat);
            totalPixelsList.add(totalPixels);
        }

        this.pixelCountForEachSuperpixel = new MatOfInt();
        this.pixelCountForEachSuperpixel.fromList(totalPixelsList);
        return this;
    }

    public SuperpixelImage calculateValue(Mat image_input){
        List<Integer> superpixelIDList = listOfSuperpixelsID.toList();
        List<Double> valueList = new ArrayList<Double>();
        List<Integer> totalPixelsList = new ArrayList<Integer>();

        // For each superpixel...
        for(int i = 0; i < superpixelIDList.size(); i++){
            int id = superpixelIDList.get(i);

            // Use the id to filter the mat to get 1,0 mat
            // 1 = pixel of this superpixel
            Mat booleanMat = new Mat();
            Core.compare(superpixelsID,new Scalar(id),booleanMat,Core.CMP_EQ);
            Core.divide(booleanMat,new Scalar(255),booleanMat);

            // Multiply to get the input image and the 1,0 mat to filter out the value
            Mat valuesMat = new Mat();
            Core.multiply(booleanMat,image_input,valuesMat);

            // Calculate the average value for each superpixel
            double totalValue = Core.sumElems(valuesMat).val[0];
            int totalPixels = Core.countNonZero(booleanMat);
            valueList.add(totalValue / totalPixels);
            totalPixelsList.add(totalPixels);
        }

        this.pixelCountForEachSuperpixel = new MatOfInt();
        this.pixelCountForEachSuperpixel.fromList(totalPixelsList);
        this.valueOfEachSuperpixel = new MatOfDouble();
        this.valueOfEachSuperpixel.fromList(valueList);
        Core.normalize(valueOfEachSuperpixel,valueOfEachSuperpixel,0,255,Core.NORM_MINMAX);
        return this;
    }

    public Mat getValueMat(){
        List<Integer> superpixelIDList = listOfSuperpixelsID.toList();
        List<Double> superpixelValueList = valueOfEachSuperpixel.toList();
        Mat result_mat = Mat.zeros(superpixelsID.size(),CvType.CV_8UC1);

        // For each superpixel...
        for(int i = 0; i < superpixelIDList.size(); i++){
            int id = superpixelIDList.get(i);

            // Use the id to filter the mat to get 1,0 mat
            // 1 = pixel of this superpixel
            Mat booleanMat = new Mat();
            Core.compare(superpixelsID,new Scalar(id),booleanMat,Core.CMP_EQ);
            Core.divide(booleanMat,new Scalar(255),booleanMat);

            // Multiply to convert all 1 to the value of that superpixel
            Mat valuesMat = new Mat();
            Core.multiply(booleanMat, new Scalar(superpixelValueList.get(i)), valuesMat);

            //Put this value matrix to the result matrix
            Core.add(valuesMat, result_mat, result_mat);
        }

        return result_mat;
    }

    public Bitmap getValueBitmap(){
        Mat result_mat = this.getValueMat();

        // Change the value matrix to Bitmap
        Bitmap result_bitmap = Bitmap.createBitmap(result_mat.width(), result_mat.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(result_mat,result_bitmap);
        return result_bitmap;
    }

    public SuperpixelImage calculateContrastValueMat(Mat input){
        if(this.valueOfEachSuperpixel == null)
            this.calculateValue(input);
        if(this.pixelCountForEachSuperpixel == null)
            this.calculatePixelCountForEachSuperpixel();

//        Mat valule_mat = this.getValueMat();
        List<Integer> superpixelIDList = listOfSuperpixelsID.toList();
        List<Point> superpixelCenterList = centersOfSuperpixels.toList();
        List<Double> superpixelValueList = valueOfEachSuperpixel.toList();
        List<Integer> pixelCountList = pixelCountForEachSuperpixel.toList();
        List<Double> contrastValueList = new ArrayList<Double>();
        Mat result_mat = Mat.zeros(superpixelsID.size(),CvType.CV_8UC1);

        // For each superpixel...
        for(int i = 0; i < superpixelIDList.size(); i++){
            double contrastValue = 0;
            double alpha = 30;
            //For each other superpixel...
            for(int j = 0; j < superpixelIDList.size(); j++) {
                if (i != j) {
                    int totalPixels = pixelCountList.get(j);

                    // Calculate the distance factor
                    Point point1 = superpixelCenterList.get(i);
                    Point point2 = superpixelCenterList.get(j);
                    double dist_x_pow = Math.pow(Math.abs(point1.x - point2.x), 2);
                    double dist_y_pow = Math.pow(Math.abs(point1.y - point2.y), 2);
                    double distance = Math.sqrt(dist_x_pow + dist_y_pow);
                    double distance_factor = Math.exp(- distance / (2.0*alpha*alpha));
//                    Log.d("SLIC distance ","distance = "+distance);
//                    Log.d("SLIC distance_factor ","distance_factor = "+distance_factor);

                    // Calculate the difference
                    double value_difference = Math.abs(superpixelValueList.get(i) - superpixelValueList.get(j));

                    // Add the contrast value to the list
                    contrastValue += totalPixels * distance_factor * value_difference;
                }
            }
            contrastValueList.add(contrastValue);
        }
        Log.d("SLIC contrastValueList ","contrastValueList = "+contrastValueList);
        this.contrastValueOfEachSuperpixel = new MatOfDouble();
        this.contrastValueOfEachSuperpixel.fromList(contrastValueList);
        DebugTools.saveMatToString("SLIC_contrastValueOfEachSuperpixel_before_normalize",contrastValueOfEachSuperpixel);
        Core.normalize(contrastValueOfEachSuperpixel,contrastValueOfEachSuperpixel,0,255,Core.NORM_MINMAX);
//        Core.multiply(contrastValueOfEachSuperpixel,new Scalar(255), contrastValueOfEachSuperpixel);
        DebugTools.saveMatToString("SLIC_contrastValueOfEachSuperpixel_after_normalize",contrastValueOfEachSuperpixel);
        return this;
    }

    public Mat getContrastValueMat(){
        List<Integer> superpixelIDList = listOfSuperpixelsID.toList();
        List<Double> superpixelContrastValueList = contrastValueOfEachSuperpixel.toList();
        Mat result_mat = Mat.zeros(superpixelsID.size(),CvType.CV_8UC1);

        // For each superpixel...
        for(int i = 0; i < superpixelIDList.size(); i++){
            int id = superpixelIDList.get(i);

            // Use the id to filter the mat to get 1,0 mat
            // 1 = pixel of this superpixel
            Mat booleanMat = new Mat();
            Core.compare(superpixelsID,new Scalar(id),booleanMat,Core.CMP_EQ);
            Core.divide(booleanMat,new Scalar(255),booleanMat);

            // Multiply to convert all 1 to the value of that superpixel
            Mat valuesMat = new Mat();
            Core.multiply(booleanMat, new Scalar(superpixelContrastValueList.get(i)), valuesMat);

            //Put this value matrix to the result matrix
            Core.add(valuesMat, result_mat, result_mat);
        }

        return result_mat;
    }

    public Bitmap getContrastValueBitmap(){
        Mat result_mat = this.getContrastValueMat();

        // Change the value matrix to Bitmap
        Bitmap result_bitmap = Bitmap.createBitmap(result_mat.width(), result_mat.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(result_mat,result_bitmap);
        return result_bitmap;
    }

    public Bitmap createBitmapWithBoundary(Bitmap input){
        // For error checking
        if(superpixelsID.size().width!=input.getWidth() || superpixelsID.size().height!=input.getHeight())
            return null;

        Mat im = new Mat(input.getWidth(),input.getHeight(),CvType.CV_8U);
        Utils.bitmapToMat(input,im);

        superpixelsID.convertTo(superpixelsID, CvType.CV_32F);
        Mat gx = new Mat();
        Mat gy = new Mat();
        Mat grad = new Mat();
        Imgproc.filter2D(superpixelsID, gx, -1, sobel);
        Imgproc.filter2D(superpixelsID, gy, -1, sobel.t());
        Core.magnitude(gx, gy, grad);

        Core.compare(grad,new Scalar(0.0001),grad,Core.CMP_GT);
        Core.divide(grad,new Scalar(255),grad);
        Mat show = new Mat();
        Core.subtract(Mat.ones(grad.size(),grad.type()),grad,show);
        show.convertTo(show, CvType.CV_8U);

        Vector<Mat> rgb = new Vector<Mat>(3);

        Utils.bitmapToMat(input, im);

        im.convertTo(im,CvType.CV_8UC3);

        Core.split(im, rgb);
        for (int i = 0; i < 3; i++)
            rgb.set(i,rgb.get(i).mul(show));

        Mat outputMat = new Mat();
        Core.merge(rgb, outputMat);

        Bitmap output = Bitmap.createBitmap(outputMat.width() , outputMat.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(outputMat, output);
        return output;
    }

    public void calculateDisplacement(MatOfPoint2f point1, MatOfPoint2f point2, MatOfByte resultStatus) {
        List<Point> pointList1 = new ArrayList<Point>();
        pointList1 = point1.toList();
        List<Point> pointList2 = new ArrayList<Point>();
        pointList2 = point2.toList();
        List<Byte> status = new ArrayList<Byte>();
        status = resultStatus.toList();

        List<Integer> superpixelsIDList = new ArrayList<Integer>();
        superpixelsIDList = listOfSuperpixelsID.toList();

        List<Point> displacementOfEachSuperpixelList = new ArrayList<Point>();
//        List<Point> vectorsForEachSuperpixelList = new ArrayList<Point>();
        List<List<Point>> vectorsForEachSuperpixelList = new ArrayList<List<Point>>();
        List<Integer> superpixelsNumberOfVectorList = new ArrayList<Integer>();
        for(int i=0;i<superpixelsIDList.size();i++) {
//            vectorsForEachSuperpixelList.add(new Point(0,0));
            vectorsForEachSuperpixelList.add(new ArrayList<Point>());
            displacementOfEachSuperpixelList.add(new Point(0,0));
            superpixelsNumberOfVectorList.add(0);
        }
        Point pt, pt2;
        for (int i = 0; i < status.size() - 1; i++) {
            if (status.get(i) == 1) {
                pt = pointList1.get(i);
                pt2 = pointList2.get(i);

                int superpixelsID = superpixelsIDList.indexOf((int)this.superpixelsID.get((int) pt.y, (int) pt.x)[0]);
//                int totalVector = superpixelsNumberOfVectorList.get(superpixelsID);
//
//                Point originalDisplacement = vectorsForEachSuperpixelList.get(superpixelsID);
//                Point totalDisplacement = new Point(originalDisplacement.x*totalVector,originalDisplacement.y*totalVector);
//                Point newDisplacement = new Point(pt2.x-pt.x,pt2.y-pt.y);
//
//                totalVector++;
//                vectorsForEachSuperpixelList.set(superpixelsID, new Point((newDisplacement.x+totalDisplacement.x)/totalVector,(newDisplacement.y+totalDisplacement.y)/totalVector));
//                superpixelsNumberOfVectorList.set(superpixelsID,totalVector);
//
                Point newVector = new Point(pt2.x-pt.x,pt2.y-pt.y);
                vectorsForEachSuperpixelList.get(superpixelsID).add(newVector);
            }
        }

        // Calculate the average without using the extreme cases, outlier reduction
        VectorComparator vectorComparator = new VectorComparator();
        for(int i=0;i<superpixelsIDList.size();i++) {
            List<Point> vectorsInASuperpixel = vectorsForEachSuperpixelList.get(i);
            String listB4Sort = "";
            for(int j=0;j<vectorsInASuperpixel.size();j++)
                listB4Sort += "("+vectorsInASuperpixel.get(j).x+","+vectorsInASuperpixel.get(j).y+"), ";

            Collections.sort(vectorsInASuperpixel, vectorComparator);

            int numberOfVectors = vectorsInASuperpixel.size();
            int quaterIndex = numberOfVectors/4;

            Double totalX = 0.0;
            Double totalY = 0.0;
            int numberOfVectorUsed = 0;
            for(int j=0;j<numberOfVectors;j++){
                totalX += vectorsInASuperpixel.get(j).x;
                totalY += vectorsInASuperpixel.get(j).y;
                numberOfVectorUsed++;
            }
            if(numberOfVectorUsed>0)
                displacementOfEachSuperpixelList.set(i,new Point(totalX/numberOfVectorUsed,totalY/numberOfVectorUsed));

            String list = "";
            for(int j=quaterIndex;j<quaterIndex*3;j++)
                list += "("+vectorsInASuperpixel.get(j).x+","+vectorsInASuperpixel.get(j).y+"), ";
            Log.d("Superpixel"+i,"listB4Sort: "+listB4Sort);
            Log.d("Superpixel"+i,"list: "+list);
            Log.d("Superpixel"+i,"numberOfVectors: "+numberOfVectors+"\tnumberOfVectorUsed: "+numberOfVectorUsed+"\tTotalPoint: "+totalX+","+totalY+"\tPoint: "+totalX/numberOfVectorUsed+","+totalY/numberOfVectorUsed);
        }

        this.displacementOfSuperpixels = new MatOfPoint2f();
//        this.displacementOfSuperpixels.fromList(vectorsForEachSuperpixelList);
        this.displacementOfSuperpixels.fromList(displacementOfEachSuperpixelList);
    }

    public Point getDisplacement(int x, int y){
        List<Integer> superpixelsIDList = new ArrayList<Integer>();
        superpixelsIDList = listOfSuperpixelsID.toList();

        List<Point> superpixelsDisplacementList = new ArrayList<Point>();
        superpixelsDisplacementList = displacementOfSuperpixels.toList();

        int superpixelsID = superpixelsIDList.indexOf((int)this.superpixelsID.get(y,x)[0]);
        if(superpixelsID==-1) {
//            Log.d("SuperpixelImage.getDisplacement fail!!!!!!!", "this.superpixelsID.get(y,x)[0]: " + this.superpixelsID.get(y,x)[0]);
//            Log.d("SuperpixelImage.getDisplacement fail!!!!!!!", "x: " + x);
//            Log.d("SuperpixelImage.getDisplacement fail!!!!!!!", "y: " + y);
            return new Point(0,0);
        }
        return superpixelsDisplacementList.get(superpixelsID);
    }

    public void setDisplacement(MatOfPoint2f point1, MatOfPoint2f point2, MatOfByte resultStatus) {
        List<Point> pointList1 = new ArrayList<Point>();
        pointList1 = point1.toList();
        List<Point> pointList2 = new ArrayList<Point>();
        pointList2 = point2.toList();
        List<Byte> status = new ArrayList<Byte>();
        status = resultStatus.toList();

//        List<Integer> superpixelsIDList = new ArrayList<Integer>();
//        superpixelsIDList = listOfSuperpixelsID.toList();

        List<Point> superpixelsDisplacementList = new ArrayList<Point>();
//        List<Integer> superpixelsNumberOfVectorList = new ArrayList<Integer>();
//        for(int i=0;i<superpixelsIDList.size();i++) {
//            superpixelsDisplacementList.add(new Point(0, 0));
//            superpixelsNumberOfVectorList.add(0);
//        }
        Point pt, pt2;
        for (int i = 0; i < status.size() - 1; i++) {
            if (status.get(i) == 1) {
                pt = pointList1.get(i);
                pt2 = pointList2.get(i);

//                if(Math.sqrt(Math.pow(Math.abs(pt.x-pt2.x),2) + Math.pow(Math.abs(pt.y-pt2.y),2)) <= CommonImageProcessing.PIXEL_DISPLACEMENT_THRESHOLD) {
                    Point displacement = new Point(pt2.x-pt.x,pt2.y-pt2.y);

                    superpixelsDisplacementList.add(displacement);
//                }
            }
            else
                superpixelsDisplacementList.add(new Point(999,999));
        }

        this.displacementOfEachPixels = new MatOfPoint2f();
        this.displacementOfEachPixels.fromList(superpixelsDisplacementList);
    }

    public Point getPixelDisplacement(int x, int y){
        List<Point> pixelsDisplacementList = new ArrayList<Point>();
        pixelsDisplacementList = displacementOfEachPixels.toList();

        return pixelsDisplacementList.get(x+y*this.superpixelsID.width());
    }
}
