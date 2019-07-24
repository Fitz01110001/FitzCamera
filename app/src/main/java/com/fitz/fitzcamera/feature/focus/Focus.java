package com.fitz.fitzcamera.feature.focus;

import android.graphics.Rect;
import android.hardware.camera2.params.MeteringRectangle;
import android.view.View;

import com.fitz.fitzcamera.CamManager;

public class Focus {

    private String TAG = "Focus";
    protected CamManager.ICamManager mICamManager;
    private Rect mRect;


    public Focus(CamManager.ICamManager camManager) {
        mICamManager = camManager;
    }

    public void setFocus(View v){


        mICamManager.IsetFocusPreview(new MeteringRectangle[] {new MeteringRectangle(mRect, 1000)});
    }



}
