package imageProcessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Jason on 12/10/2014.
 */
public class SLIC {
//    RNG rng(12345);

//    Mat sobel = (Mat_<float>(3,3) << -1/16., -2/16., -1/16., 0, 0, 0, 1/16., 2/16., 1/16.);
    Mat sobel = new Mat(3, 3, CvType.CV_8U);
    sobel.put(0, 0, (byte) 0);
    sobel.put(0, 1, (byte) 1);
    sobel.put(0, 2, (byte) 0);
    sobel.put(1, 0, (byte) 1);
    sobel.put(1, 1, (byte) 1);
    sobel.put(1, 2, (byte) 1);
    sobel.put(2, 0, (byte) 0);
    sobel.put(2, 1, (byte) 1);
    sobel.put(2, 2, (byte) 0);
    Imgproc.dilate(outerbox, outerbox, ukernel);

    private int nx, ny;
    private int m;
    private Bitmap input;

    public SLIC(int nx, int ny, int m){
        this.nx = nx;
        this.ny = ny;
        this.m = m;
    }

    public Bitmap createBoundedBitmap(Bitmap input){
        // Read in image
        Mat im = imread(input);

        if (input == null) {
            Log.d("SLIC ERROR: ","no image data at "+input)
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
//        vector<Point2i> centers;
//        for (int i = 0; i < ny; i++)
//            for (int j = 0; j < nx; j++)
//                centers.push_back( Point2f(j*dx+dx/2, i*dy+dy/2));
        MatOfPoint2f centers = new MatOfPoint2f();
        for (int i = 0; i < ny; i++) {
            for (int j = 0; j < nx; j++) {
                centers.push_back(new MatOfPoint(new Point(j*dx+dx/2, i*dy+dy/2)));
            }
        }

        // Initialize labels and distance maps
//        vector<int> label_vec(n);
//        for (int i = 0; i < n; i++)
//            label_vec[i] = i*255*255/n;
        ArrayList<Integer> label_vec = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++)
            label_vec.set(i,i*255*255/n);

//        Mat labels = -1*Mat.ones(imlab.size(), CvType.CV_32S));
//        Mat dists = -1*Mat.ones(imlab.size(), CvType .CV_32F);
        Scalar negativeOne = new Scalar(-1);
        Mat labels = new Mat();
        Mat dists = new Mat();
        Core.multiply(Mat.ones(imlab.size(), CvType.CV_32S),negativeOne,labels);
        Core.multiply(Mat.ones(imlab.size(), CvType.CV_32S),negativeOne,labels);

        Mat window;
        Point p1, p2;
        float[] p1_lab = new float[3];
        float[] p2_lab = new float[3];

        // Iterate 10 times. In practice more than enough to converge
        for (int i = 0; i < 10; i++) {
            // For each center...
            for (int c = 0; c < n; c++)
            {
                int label = label_vec.get(c);
                p1 = centers.toArray()[c];
                p1_lab = imlab.at<Vec3f>(p1);
                int xmin = (int) Math.max(p1.x - S, 0);
                int ymin = (int) Math.max(p1.y-S, 0);
                int xmax = (int) Math.min(p1.x+S, w-1);
                int ymax = (int) Math.min(p1.y+S, h-1);

                // Search in a window around the center
                window = new Mat(im, new Range(ymin, ymax), new Range(xmin, xmax));

                // Reassign pixels to nearest center
                for (int i = 0; i < window.rows(); i++) {
                    for (int j = 0; j < window.cols(); j++) {
                        p2 = new Point(xmin + j, ymin + i);
                        p2_lab = imlab.at<Vec3f>(p2);
                        float d = dist(p1, p2, p1_lab, p2_lab, m, S);
                        float last_d = (float) dists.get((int)p2.x,(int)p2.y)[0];
                        if (d < last_d || last_d == -1) {
                            dists.put((int)p2.x,(int)p2.y,d);
                            labels.put((int)p2.x,(int)p2.y,label);
                        }
                    }
                }
            }
        }

        // Calculate superpixel boundaries
        labels.convertTo(labels, CvType.CV_32F);
        Mat gx, gy, grad;
        Imgproc.filter2D(labels, gx, -1, sobel);
        Imgproc.filter2D(labels, gy, -1, sobel.t());
        Core.magnitude(gx, gy, grad);
        grad = (grad > 1e-4)/255;
        Mat show = new Mat();
        Core.subtract(Mat.ones(grad.size(),CvType.CV_32F),grad,show);
        show.convertTo(show, CvType.CV_32F);

        // Draw boundaries on original image
        Vector<Mat> rgb = new Vector<Mat>(3);
        Core.split(im, rgb);
        for (int i = 0; i < 3; i++)
            rgb.set(i,rgb.get(i).mul(show));

        Core.merge(rgb, im);

        imwrite(output, 255*im);

        Bitmap output = new Bitmap();
        return output;
    }

    private float dist(Point p1, Point p2, float[] p1_lab, float[] p2_lab, float compactness, float S){
        float dl = p1_lab[0] - p2_lab[0];
        float da = p1_lab[1] - p2_lab[1];
        float db = p1_lab[2] - p2_lab[2];

        float d_lab = (float) Math.sqrt(dl*dl + da*da + db*db);

        float dx = (float) (p1.x - p2.x);
        float dy = (float) (p1.y - p2.y);

        float d_xy = (float) Math.sqrt(dx * dx + dy * dy);

        return d_lab + compactness/S * d_xy;
    }
}
