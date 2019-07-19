package com.fitz.fitzcamera.feature.zoom;

import android.graphics.Rect;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.util.Log;
import android.widget.TextView;

import com.fitz.fitzcamera.CamManager;


public class Zoom {

    private String TAG = "Zoom";

    private TextView mTextView;

    /**
     * 默认后置主摄
     */
    private String mDefaultCameraId = "0";

    private final String deviceModeLG = "LM-X625N";

    private final String deviceModeHW = "LYA-AL00";

    /**
     * 广角镜头id，LG：2 HW：3
     */
    private String mWideCameraId;

    /**
     * 广角和主摄之间的zoom差值
     */
    private Float zoomDiff;

    /**
     * 当前zoom值
     */
    private Rect mCurrentRect;

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    private CamManager.ICamManager mICamManager;

    public Zoom(CamManager.ICamManager camManager) {
        mICamManager = camManager;
    }

    public void setZoom(String currentDeviceMode, float zoomRatio) {
        mICamManager.IupdateZoomText(zoomRatio);
        if (currentDeviceMode.equals(deviceModeLG)) {
            setZoomRatioInLG(zoomRatio);
        } else if (currentDeviceMode.equals(deviceModeHW)) {
            setZoomRatioInHW(zoomRatio);
        }
    }

    /**
     * LG 1885的zoom实现(手动切换镜头)
     */
    private void setZoomRatioInLG(float zoomRatio) {
        mCameraId = mICamManager.IgetCameraId();
        mWideCameraId = mICamManager.IgetWideCameraId();
        zoomDiff = mICamManager.IgetZoomDiff();
        Log.d(TAG, "setZoomRatioInLG:" + mCameraId + " zoomRatio:" + zoomRatio);
        if (!mCameraId.equals(mWideCameraId) && zoomRatio >= 1.0f) {
            //非广角镜头的zoom
            mCurrentRect = mICamManager.IcropRegionForZoom(zoomRatio);
            mICamManager.IsetZoomPreview(mCurrentRect);
        } else if (!mCameraId.equals(mWideCameraId) && zoomRatio < 1.0f) {
            //非广角镜头zoom到1.0以下，应切换到广角。并在启动广角时，设置zoom=1+zoomdiff
            mICamManager.IswitchCamera(mWideCameraId, new CamManager.CameraInfoCallback() {
                @Override
                public void cameraFacing(int facing) {

                }

                @Override
                public void cameraDeviceOnConfigured(CaptureRequest.Builder builder) {
                    Log.d(TAG, "cameraDeviceOnConfigured builder.set zoomDiff");
                    mCurrentRect = mICamManager.IcropRegionForZoom(1 + zoomDiff);
                    builder.set(CaptureRequest.SCALER_CROP_REGION, mCurrentRect);
                }
            });
        } else if (mCameraId.equals(mWideCameraId) && zoomRatio >= 1.0f) {
            //广角镜头zoom到1X以上，广角镜头切到主摄
            mICamManager.IswitchCamera(mDefaultCameraId, null);
        } else if (mCameraId.equals(mWideCameraId) && zoomRatio < 1.0f) {
            //广角镜头下zoom,广角镜头zoom范围也是 1.0~max,zoomRatio需要换算
            zoomRatio += zoomDiff;
            mCurrentRect = mICamManager.IcropRegionForZoom(zoomRatio);
            mICamManager.IsetZoomPreview(mCurrentRect);
        }
    }

    /**
     * Mate20 pro的zoom实现,直接传值即可(0.6~10)
     */
    private void setZoomRatioInHW(float zoomRatio) {
        Log.d(TAG, mCameraId + " zoomRatio:" + zoomRatio);
        mCurrentRect = mICamManager.IcropRegionForZoom(zoomRatio);
        mICamManager.IsetZoomPreview(mCurrentRect);
    }


}
