package com.fitz.camerax_demo;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.DisplayMetrics;
import android.util.Size;

import java.util.Arrays;

public class CamXManager {

    private Context mContext;
    private CameraManager mCameraManager;
    private CameraCharacteristics mCameraCharacteristics;

    public CamXManager(Context context) {
        mContext = context;
        mCameraManager = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                mCameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public Size getDefaultSize(){
        StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        //取第一个值，为默认分辨率
        Size mDefaultSize = Arrays.asList(map.getOutputSizes(ImageFormat.JPEG))
                             .get(0);
        return mDefaultSize;
    }

    public Size getScreenSize(){
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        return new Size(screenWidth,screenHeight);
    }
}
