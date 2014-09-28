package imageProcessing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

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
}
