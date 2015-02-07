package imageProcessing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Jason on 9/9/2014.
 */
public class LSH {
    private static float alpha_x;
    private static float alpha_y;
    private static int imgWidth;
    private static int imgHeight;

    private static int bin_number = 64;
    private static float alpha = (float) 0.15;
    private static float k = (float) 0.1;

    /**
     * LSH calculation
     *
     * @param image bitmap need to be perform IIF
     * @return 2D array of histogram [imgWidth][imgHeight][bin_number]
     */
    public static float[][] LocalitySensitiveHistograms(Bitmap image) {
        // initialize
        imgWidth = image.getWidth();
        imgHeight = image.getHeight();
        alpha_x = (float) Math.exp(-Math.sqrt(2) / (alpha * imgWidth));
        alpha_y = (float) Math.exp(-Math.sqrt(2) / (alpha * imgHeight));
        float[][] q_mtx = new float[imgWidth * imgHeight][bin_number];

        // initialize own value into histogram
        for (int x = 0; x < imgWidth; x++)
            for (int y = 0; y < imgHeight; y++) {
                int own_bin = findBin(image, x, y);
                for (int i = 0; i < bin_number; i++) {
                    q_mtx[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
                }
            }
        float[][] histogram = new float[imgWidth * imgHeight][bin_number];
        for (int i = 0; i < q_mtx.length; i++) {
            histogram[i] = Arrays.copyOf(q_mtx[i], q_mtx[i].length);
        }

        // Compute according x dimension
        // Left to right
        float[][] histogramL = new float[imgWidth * imgHeight][bin_number];
        for (int i = 0; i < histogram.length; i++) {
            histogramL[i] = Arrays.copyOf(histogram[i], histogram[i].length);
        }
        for (int y = 0; y < imgHeight; y++)
            for (int x = 0; x < imgWidth; x++) {
                float value = CommonImageProcessing.getValue(image, x, y);
                int own_bin = (int) Math.round(value / (255.0 / bin_number));
                if (x == 0) {
                    for (int i = 0; i < bin_number; i++) {
                        histogramL[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
                    }
                } else {
                    for (int i = 0; i < bin_number; i++) {
                        histogramL[x + (y * imgWidth)][i] = alpha_x * histogramL[(x - 1) + (y * imgWidth)][i] + ((own_bin == i) ? 1 : 0);
                    }
                }
            }

        // Right to left
        float[][] histogramR = new float[imgWidth * imgHeight][bin_number];
        for (int i = 0; i < histogram.length; i++) {
            histogramR[i] = Arrays.copyOf(histogram[i], histogram[i].length);
        }
        for (int y = 0; y < imgHeight; y++)
            for (int x = imgWidth - 1; x >= 0; x--) {
                float value = CommonImageProcessing.getValue(image, x, y);
                int own_bin = (int) Math.round(value / (255.0 / bin_number));
                if (x == imgWidth - 1) {
                    for (int i = 0; i < bin_number; i++) {
                        histogramR[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
                    }
                } else {
                    for (int i = 0; i < bin_number; i++) {
                        histogramR[x + (y * imgWidth)][i] = alpha_x * histogramR[(x + 1) + (y * imgWidth)][i] + ((own_bin == i) ? 1 : 0);
                    }
                }
            }

        // Combine two histogram
        for (int x = 0; x < imgWidth; x++)
            for (int y = 0; y < imgHeight; y++)
                for (int i = 0; i < bin_number; i++) {
                    histogram[x + (y * imgWidth)][i] = histogramL[x + (y * imgWidth)][i] + histogramR[x + (y * imgWidth)][i] - q_mtx[x + (y * imgWidth)][i];
                }

        // Compute according y dimension
        // Top to bottom
        float[][] histogramU = new float[imgWidth * imgHeight][bin_number];
        for (int i = 0; i < histogram.length; i++) {
            histogramU[i] = Arrays.copyOf(histogram[i], histogram[i].length);
        }
        for (int x = 0; x < imgWidth; x++)
            for (int y = 0; y < imgHeight; y++) {
                float value = CommonImageProcessing.getValue(image, x, y);
                int own_bin = (int) Math.round(value / (255.0 / bin_number));
                if (y == 0) {
                    for (int i = 0; i < bin_number; i++) {
                        histogramU[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
                    }
                } else {
                    for (int i = 0; i < bin_number; i++) {
                        histogramU[x + (y * imgWidth)][i] = alpha_y * histogramU[x + ((y - 1) * imgWidth)][i] + ((own_bin == i) ? 1 : 0);
                    }
                }
            }

        // Bottom to top
        float[][] histogramD = new float[imgWidth * imgHeight][bin_number];
        for (int i = 0; i < histogram.length; i++) {
            histogramD[i] = Arrays.copyOf(histogram[i], histogram[i].length);
        }
        for (int x = 0; x < imgWidth; x++)
            for (int y = imgHeight - 1; y >= 0; y--) {
                float value = CommonImageProcessing.getValue(image, x, y);
                int own_bin = (int) Math.round(value / (255.0 / bin_number));
                if (y == imgHeight - 1) {
                    for (int i = 0; i < bin_number; i++) {
                        histogramD[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
                    }
                } else {
                    for (int i = 0; i < bin_number; i++) {
                        histogramD[x + (y * imgWidth)][i] = alpha_y * histogramD[x + ((y + 1) * imgWidth)][i] + ((own_bin == i) ? 1 : 0);
                    }
                }
            }

        // Combine two histogram
        for (int x = 0; x < imgWidth; x++)
            for (int y = 0; y < imgHeight; y++)
                for (int i = 0; i < bin_number; i++) {
                    histogram[x + (y * imgWidth)][i] = histogramU[x + (y * imgWidth)][i] + histogramD[x + (y * imgWidth)][i] - q_mtx[x + (y * imgWidth)][i];
                }

        Log.d("VALUE CHECKING!!!!!!!!!!", "alphaX: " + alpha_x);
        Log.d("VALUE CHECKING!!!!!!!!!!", "alphaY: " + alpha_y);

        // Normalize histogram
        for (int x = 0; x < imgWidth; x++)
            for (int y = 0; y < imgHeight; y++) {
                float total = 0;
                for (int i = 0; i < bin_number; i++) {
                    total += histogram[x + (y * imgWidth)][i];
                }
//                Log.d("VALUE CHECKING!!!!!!!!!!", "Location: "+x+","+y+"\tValue: "+total);
                for (int i = 0; i < bin_number; i++) {
                    histogram[x + (y * imgWidth)][i] = histogram[x + (y * imgWidth)][i] / total * 255;
                }
            }

        return histogram;
    }

    /**
     * IIF calculation including LSH calculation
     *
     * @param bitmap bitmap need to be perform IIF
     * @return bitmap that illumination being removed
     */
    public static Bitmap IIF(Bitmap bitmap) {
        float[][] histogram = LocalitySensitiveHistograms(bitmap); // construct LSH
        imgWidth = bitmap.getWidth();
        imgHeight = bitmap.getHeight();
        int[] own_bin = new int[imgWidth * imgHeight];
        float[] values = new float[imgWidth * imgHeight];

        // initialize own bin into array
        for (int x = 0; x < imgWidth; x++)
            for (int y = 0; y < imgHeight; y++) {
                int bin = findBin(bitmap, x, y);
                own_bin[x + (y * imgWidth)] = bin;
            }

        // Calculate the illumination invariant features
        for (int x = 0; x < imgWidth; x++) {
            for (int y = 0; y < imgHeight; y++) {
                float value = 0;
                for (int i = 0; i < bin_number; i++) {
                    float b_bp2 = (float) Math.pow((i - own_bin[x + (y * imgWidth)]), 2);
                    float maxKrp2 = (float) Math.pow((k * CommonImageProcessing.getValue(bitmap, x, y)), 2);
                    if (maxKrp2 < k)
                        maxKrp2 = k;
                    value += Math.exp(-b_bp2 / (2 * maxKrp2)) * histogram[x + (y * imgWidth)][i];
                }
                values[x + (y * imgWidth)] = value;
            }
        }


        // Construct the bitmap with the values array
        Bitmap result = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < imgWidth; x++)
            for (int y = 0; y < imgHeight; y++) {
                int value = Math.round(values[x + (y * imgWidth)]);
                if (value < 0 || value > 255)
                    Log.d("VALUE TOO LARGE!!!!!!!!!!!!!", "Location: " + x + "," + y + "\tValue: " + value);
                result.setPixel(x,y, Color.rgb(value, value, value));
            }
        return result;
    }

    /**
     * Method to find the corresponding bin of a specific pixel in an image
     *
     * @param image image bitmap
     * @param x     x coordinate
     * @param y     y coordinate
     */
    private static int findBin(Bitmap image, int x, int y) {
        float value = CommonImageProcessing.getValue(image, x, y);
        int bin;
        bin = (int) Math.round(value / (255.0 / bin_number));
        return bin;
    }

    /**
     * LSH recursion for calculate of left to right
     *
     * @param histogram histogram with all Q set
     * @param image     image bitmap
     * @param x         should be imgWidth of image
     * @param y         the row that need to perform this calculation
     */
    private static void LSH_left(float[][] histogram, int x, int y, Bitmap image) {
        if ((x - 1) < 0 || (y - 1) < 0 || (x - 1) > (image.getWidth() - 1) || (y - 1) > (image.getHeight() - 1))
            Log.d("GET PIXEL ERROR!!!!!!!!!!!!", "X,Y is " + x + "," + y + "\tmax X,Y is" + image.getWidth() + "," + image.getHeight());
        int color = image.getPixel(x,y);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        float value = (float) (0.299 * red + 0.587 * green + 0.114 * blue);
//        float value = CommonImageProcessing.getValue(image, x,y);
        int own_bin = (int) Math.round(value / (255.0 / bin_number));
        if (x == 1) {
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
            }
        } else {
            LSH_left(histogram, x - 1, y, image);
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = alpha_x * histogram[(x - 1) * y][i] + ((own_bin == i) ? 1 : 0);
            }
        }
    }

    /**
     * LSH recursion for calculate from right to left
     *
     * @param histogram histogram with all Q set
     * @param image     image bitmap
     * @param x         should be 1
     * @param y         the row that need to perform this calculation
     */
    private static void LSH_right(float[][] histogram, int x, int y, Bitmap image) {
        float value = CommonImageProcessing.getValue(image, x, y);
        int own_bin = (int) Math.round(value / (255.0 / bin_number));
        if (x == image.getWidth()) {
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
            }
        } else {
            LSH_right(histogram, x + 1, y, image);
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = alpha_x * histogram[(x + 1) * y][i] + ((own_bin == i) ? 1 : 0);
            }
        }
    }

    /**
     * LSH recursion for calculate from top to bottom
     *
     * @param histogram histogram with all Q set
     * @param image     image bitmap
     * @param x         the column that need to perform this calculation
     * @param y         should be imgHeight of image
     */
    private static void LSH_up(float[][] histogram, int x, int y, Bitmap image) {
        float value = CommonImageProcessing.getValue(image, x, y);
        int own_bin = (int) Math.round(value / (255.0 / bin_number));
        if (y == 1) {
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
            }
        } else {
            LSH_up(histogram, x, y - 1, image);
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = alpha_y * histogram[x * (y - 1)][i] + ((own_bin == i) ? 1 : 0);
            }
        }
    }

    /**
     * LSH recursion for calculate from top to bottom
     *
     * @param histogram histogram with all Q set
     * @param image     image bitmap
     * @param x         the column that need to perform this calculation
     * @param y         should be 1
     */
    private static void LSH_down(float[][] histogram, int x, int y, Bitmap image) {
        float value = CommonImageProcessing.getValue(image, x, y);
        int own_bin = (int) Math.round(value / (255.0 / bin_number));
        if (y == image.getHeight()) {
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = ((own_bin == i) ? 1 : 0);
            }
        } else {
            LSH_down(histogram, x, y + 1, image);
            for (int i = 0; i < bin_number; i++) {
                histogram[x + (y * imgWidth)][i] = alpha_y * histogram[x * (y + 1)][i] + ((own_bin == i) ? 1 : 0);
            }
        }
    }
}
