package tools;


import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jason on 10/11/2014.
 */
public class DebugTools {
    public static void saveMatToString(String txt_name, Mat mat){
        String filename = txt_name + ".txt";
        String string = mat.dump();
        try {
            File file = new File("/sdcard/"+filename);
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
