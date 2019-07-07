package com.fitz.fitzcamera;

import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.os.Environment.DIRECTORY_DCIM;

/**
 * save image to file
 */
public class SaveImage implements Runnable {

    private static String TAG = "SaveImage";

    private Image mImage;
    private String mImageName;

    private static File imageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);


    SaveImage(Image image, String imageName) {
        Log.d(TAG, "SaveImage: " + imageName);
        mImage = image;
        mImageName = imageName;
    }

    /**
     * get pic name by time :
     * FIMG_20190101_170259
     */
    public static String getImageName() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("FIMG_")
                     .append(year)
                     .append(month)
                     .append(day)
                     .append("_")
                     .append(hour)
                     .append(min)
                     .append(sec)
                     .append(".jpg");
        return stringBuilder.toString();
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        // 首先判断文件夹是否存在
        if (!imageDir.exists()) {
            Log.e(TAG, "文件夹不存在!!?");
        } else {
            File finalImage = new File(imageDir, mImageName);

            Uri fileImageFilePath = Uri.fromFile(finalImage);
            try {
                // 实例化对象：文件输出流
                FileOutputStream mFileOutputStream = new FileOutputStream(finalImage);
                // 写入文件
                mFileOutputStream.write(bytes);
                // 清空输出流缓存
                mFileOutputStream.flush();
                // 关闭输出流
                mFileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
