package com.fitz.camerax_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import android.graphics.Matrix;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.util.Rational;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import static android.os.Environment.DIRECTORY_DCIM;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Fitz-cameraX";
    private TextureView viewFinder;
    private Preview preview;
    private ImageCapture imageCapture;
    private FloatingActionButton mCameraSwitch;
    private FloatingActionButton captureButton;
    private CamXManager mCamXManager;
    private static File imageDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        viewFinder = findViewById(R.id.view_camera);
        captureButton = findViewById(R.id.button_shutter);
        mCameraSwitch = findViewById(R.id.switchCam);
        mCameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraX.unbindAll();
                //startCamera(CameraX.LensFacing.FRONT);
            }
        });

        mCamXManager = new CamXManager(this);
        // Request camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            viewFinder.post(new Runnable() {
                @Override
                public void run() {
                    // start camera
                    startCamera(CameraX.LensFacing.BACK);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // Permission Granted 授予权限
            //处理授权之后逻辑
            viewFinder.post(new Runnable() {
                @Override
                public void run() {
                    // start camera
                    startCamera(CameraX.LensFacing.BACK);
                }
            });
        } else {
            // Permission Denied 权限被拒绝
            requestCameraPermission();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startCamera(CameraX.LensFacing lensFacing) {
        // Create configuration object for the viewfinder use case
        //.setTargetAspectRatio(new Rational(19, 9))
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setLensFacing(lensFacing)
                .setTargetResolution(mCamXManager.getScreenSize())
                .build();

        // Build the viewfinder use case
        preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                // To update the SurfaceTexture, we have to remove it and re-add it
                ViewGroup viewParent = (ViewGroup) viewFinder.getParent();
                viewParent.removeView(viewFinder);
                viewParent.addView(viewFinder);

                viewFinder.setSurfaceTexture(output.getSurfaceTexture());
                updateTransform();
            }
        });

        // We don't set a resolution for image capture; instead, we
        // select a capture mode which will infer the appropriate
        // resolution based on aspect ration and requested mode
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setTargetResolution(mCamXManager.getScreenSize())
                                                                                .setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY)
                                                                                .build();

        // Build the image capture use case and attach button click listener
        imageCapture = new ImageCapture(imageCaptureConfig);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(imageDir, "${System.currentTimeMillis()}.jpg");
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        Log.d(TAG, "SaveImage success");
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        Log.d(TAG, "SaveImage error");
                    }
                });
            }
        });


        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, imageCapture);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();
        // Compute the center of the view finder
        float centerX = viewFinder.getWidth() / 2.0f;
        float centerY = viewFinder.getHeight() / 2.0f;

        // Correct preview output to account for display rotation
        float rotationDegrees = 0;
        switch (viewFinder.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegrees = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270;
                break;
        }

        matrix.postRotate(-rotationDegrees, centerX, centerY);
        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
}
