package tools;

import org.opencv.core.Point;

import java.util.Comparator;

/**
 * Created by Jason on 2/6/2015.
 */
public class VectorComparator implements Comparator<Point>{

    @Override
    public int compare(Point vector1, Point vector2) {
        if(MathHelper.VectorLength(MathHelper.zeroPoint,vector1)>MathHelper.VectorLength(MathHelper.zeroPoint,vector2))
            return 1;
        else if(MathHelper.VectorLength(MathHelper.zeroPoint,vector1)<MathHelper.VectorLength(MathHelper.zeroPoint,vector2))
            return -1;
        else
            return 0;
    }
}
