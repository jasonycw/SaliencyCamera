package tools;

import org.opencv.core.Point;
/**
 * Created by Jason on 2/6/2015.
 */
public class MathHelper {
    public static Point zeroPoint = new Point(0,0);

    public static double VectorLength(Point v1, Point v2){
        return Math.sqrt(Math.pow(Math.abs(v1.x-v2.x),2) + Math.pow(Math.abs(v1.y-v2.y),2));
    }

    public static double VectorLength(Point vector){
        return Math.sqrt(Math.pow(Math.abs(vector.x),2) + Math.pow(Math.abs(vector.y),2));
    }
}
