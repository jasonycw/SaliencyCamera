package imageProcessing;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import tools.DebugTools;

/**
 * Created by Jason on 12/10/2014.
 */
public class SLIC {
//    RNG rng(12345);

//    Mat sobel = (Mat_<float>(3,3) << -1/16., -2/16., -1/16., 0, 0, 0, 1/16., 2/16., 1/16.);
    Mat sobel;
    private int nx, ny;
    private int m;
    Context context;

    public SLIC(int nx, int ny, int m){
        this.nx = nx;
        this.ny = ny;
        this.m = m;
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

    public void setContext(Context context){
        this.context = context;
    }

    public Bitmap createBoundedBitmap(Bitmap input){
        // Read in image
//        Mat im = imread(input);
        Mat im = new Mat(input.getWidth(),input.getHeight(),CvType.CV_8U);
        Utils.bitmapToMat(input,im);

        if (input == null) {
            Log.d("SLIC ERROR: ","no image data at "+input);
            return null;
        }

        // Scale to [0,1] and l*a*b colorspace
        im.convertTo(im, CvType.CV_32F, 1/255.);
        Mat imlab = new Mat();
        Imgproc.cvtColor(im, imlab, Imgproc.COLOR_BGR2Lab);

        int h = im.rows();
        int w = im.cols();
        int n = nx*ny;

        float dx = w / (float)nx;
        float dy = h / (float)ny;
        int S = (int) ((dx + dy + 1)/2); // window width

        // Initialize centers
        MatOfPoint2f centers = new MatOfPoint2f();
        List<Point> centersList = new ArrayList<Point>();
        for (int i = 0; i < ny; i++) {
            for (int j = 0; j < nx; j++) {
                centersList.add(new Point(j * dx + dx / 2, i * dy + dy / 2));
            }
        }
        centers.fromList(centersList);

        // Initialize labels and distance maps
        MatOfInt label_vec = new MatOfInt();
        List<Integer> label_vec_list = new ArrayList<Integer>();
        for (int i = 0; i < n; i++)
            label_vec_list.add(i * 255 * 255 / n);
        label_vec.fromList(label_vec_list);

        Scalar negativeOne = new Scalar(-1);
        Mat labels = new Mat();
        Mat dists = new Mat();
        Core.multiply(Mat.ones(imlab.size(), CvType.CV_32S),negativeOne,labels);
        Core.multiply(Mat.ones(imlab.size(), CvType.CV_32S),negativeOne,dists);

        Mat window;
        Point p1, p2;
        double[] p1_lab;
        double[] p2_lab;

        // Iterate 10 times. In practice more than enough to converge
        int numberOfIteration = 1;
        for (int i = 0; i < numberOfIteration; i++) {
            // For each center...
            for (int c = 0; c < n; c++)
            {
                int label = label_vec.toList().get(c);
                p1 = centers.toList().get(c);
                p1_lab = imlab.get((int)p1.y,(int)p1.x);//.at<Vec3f>(p1);
                int xmin = (int) Math.max(p1.x-S, 0);
                int ymin = (int) Math.max(p1.y-S, 0);
                int xmax = (int) Math.min(p1.x+S, w-1);
                int ymax = (int) Math.min(p1.y+S, h-1);

                // Search in a window around the center
                window = new Mat(im, new Range(ymin, ymax), new Range(xmin, xmax));

                // Reassign pixels to nearest center
                for (int row = 0; row < window.rows(); row++) {
                    for (int col = 0; col < window.cols(); col++) {
                        p2 = new Point(xmin + col, ymin + row);
                        p2_lab = imlab.get((int) p2.y, (int) p2.x);// at<Vec3f>(p2);
                        float d = dist(p1, p2, p1_lab, p2_lab, m, S);
                        float last_d = (float) dists.get((int) p2.y, (int) p2.x)[0];
                        if (d < last_d || last_d == -1) {
                            dists.put((int) p2.y, (int) p2.x, d);
                            labels.put((int) p2.y, (int) p2.x, label);
                        }
                    }
                }
            }
        }

//        Bitmap test = Bitmap.createBitmap(im.width() , im.height(), Bitmap.Config.ARGB_8888);
//        Core.multiply(im, new Scalar(255), im);
//        im.convertTo(im,CvType.CV_8U);
//        Utils.matToBitmap(im, test);
//        DebugTools.saveMatToString("SLIC_labels",labels);

        // Calculate superpixel boundaries
        labels.convertTo(labels, CvType.CV_32F);
        Mat gx = new Mat();
        Mat gy = new Mat();
        Mat grad = new Mat();
        Imgproc.filter2D(labels, gx, -1, sobel);
        Imgproc.filter2D(labels, gy, -1, sobel.t());
        Core.magnitude(gx, gy, grad);
//        Log.d("SLIC sobel ","sobel = "+gx.toString());
//        Log.d("SLIC gx ","gx = "+gx.toString());
//        Log.d("SLIC gy ","gy = "+gy.toString());
//        Log.d("SLIC grad ","grad = "+grad.toString());
//        DebugTools.saveMatToString("SLIC_sobel",sobel);
//        DebugTools.saveMatToString("SLIC_gx",gx);
//        DebugTools.saveMatToString("SLIC_gy",gy);
//        DebugTools.saveMatToString("SLIC_grad_afterMagnitude",grad);

        Core.compare(grad,new Scalar(0.0001),grad,Core.CMP_GT);
        Core.divide(grad,new Scalar(255),grad);
        Mat show = new Mat();
        Core.subtract(Mat.ones(grad.size(),grad.type()),grad,show);
        show.convertTo(show, CvType.CV_8U);

//        Log.d("SLIC grad ","grad = "+grad.toString());
//        DebugTools.saveMatToString("SLIC_grad",grad);

        // Draw boundaries on original image
        Vector<Mat> rgb = new Vector<Mat>(3);
//        Log.d("SLIC im ","im1 = "+im.toString());
//        DebugTools.saveMatToString("SLIC_im1",im);

        Utils.bitmapToMat(input,im);
//        Log.d("SLIC im ","im2 = "+im.toString());
//        DebugTools.saveMatToString("SLIC_im2",im);


        im.convertTo(im,CvType.CV_8UC3);
//        Log.d("SLIC im ","im3 = "+im.toString());
//        DebugTools.saveMatToString("SLIC_im3",im);

        Core.split(im, rgb);
//        Log.d("SLIC rgb","rgb.get(0) = "+rgb.get(0).toString());
//        Log.d("SLIC rgb","rgb.get(1) = "+rgb.get(1).toString());
//        Log.d("SLIC rgb","rgb.get(2) = "+rgb.get(2).toString());
//        Log.d("SLIC show ","show = "+show.toString());
        for (int i = 0; i < 3; i++)
            rgb.set(i,rgb.get(i).mul(show));
//        DebugTools.saveMatToString("SLIC_rgb0",rgb.get(0));
//        DebugTools.saveMatToString("SLIC_rgb1",rgb.get(1));
//        DebugTools.saveMatToString("SLIC_rgb2",rgb.get(2));
//
//        DebugTools.saveMatToString("SLIC_im4",im);
//        Log.d("SLIC labels ","labels = "+labels.toString());


        Mat outputMat = new Mat();
        Core.merge(rgb, outputMat);
//        Core.multiply(outputMat, new Scalar(255), outputMat);
//        outputMat.convertTo(outputMat,CvType.CV_8UC3);
//        show.convertTo(outputMat,CvType.CV_8U);
//        Log.d("SLIC labels ","labels = "+labels.toString());

//        Log.d("SLIC outputMat ","outputMat = "+outputMat.toString());

        Bitmap output = Bitmap.createBitmap(outputMat.width() , outputMat.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(outputMat, output);
        return output;
    }

    private float dist(Point p1, Point p2, double[] p1_lab, double[] p2_lab, float compactness, float S){
        float dl = (float) (p1_lab[0] - p2_lab[0]);
        float da = (float) (p1_lab[1] - p2_lab[1]);
        float db = (float) (p1_lab[2] - p2_lab[2]);

        float d_lab = (float) Math.sqrt(dl*dl + da*da + db*db);

        float dx = (float) (p1.x - p2.x);
        float dy = (float) (p1.y - p2.y);

        float d_xy = (float) Math.sqrt(dx * dx + dy * dy);

        return d_lab + compactness/S * d_xy;
    }
}
