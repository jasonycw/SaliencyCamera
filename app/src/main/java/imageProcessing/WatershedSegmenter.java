package imageProcessing;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jason on 5/10/2014.
 */
public class WatershedSegmenter{
    public Mat markers;

    public void setMarkers(Mat markerImage){
        if(markers == null)
            markers = new Mat();
        markerImage.convertTo(markers, CvType.CV_32SC1);
    }

    public Mat process(Mat image){
        Imgproc.watershed(image, markers);
        markers.convertTo(markers, CvType.CV_8U);
        return markers;
    }
}