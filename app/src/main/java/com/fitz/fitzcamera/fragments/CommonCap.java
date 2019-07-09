package com.fitz.fitzcamera.fragments;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fitz.fitzcamera.CamManager;
import com.fitz.fitzcamera.R;
import com.fitz.fitzcamera.ui.AutoFitTextureView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommonCap extends Fragment {
    private static final String TAG = "CommonCap";

    private CamManager mCamManager;

    private final int defaultProgress = 100;

    /**
     * 拍照用的 buttn
     */
    private Button shutter;

    private ImageButton switchCamera;

    /**
     * 预览显示view
     */
    private AutoFitTextureView mTextureView;

    private SeekBar mSeekBar;

    private TextView mZoomLevel;

    private QMUIListPopup mListPopup;


    private View.OnClickListener mButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_shutter:
                    Log.d(TAG, "点击拍照");
                    String imageUri = mCamManager.takeShot();
                    Toast.makeText(CommonCap.this.getActivity(), imageUri, Toast.LENGTH_SHORT)
                         .show();
                    break;
                case R.id.button_switchCamera:
                    initListPopupIfNeed();
                    mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                    mListPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
                    mListPopup.show(v);
                    break;
                default:
                    break;
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(TAG, "progress:" + progress);
            float zoomRatio = (float) progress / 100;
            setZoomRatioText(zoomRatio);
            mCamManager.setZoomRatio(zoomRatio);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * 設置camera切換 popupmenu
     */
    private void initListPopupIfNeed() {
        if (mListPopup == null) {
            String[] listItems = mCamManager.getCameraIDList() != null ? mCamManager.getCameraIDList() : new String[]{
                    "0",
                    "1",
            };
            List<String> data = new ArrayList<>();

            Collections.addAll(data, listItems);

            ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item, data);

            mListPopup = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, adapter);
            mListPopup.create(QMUIDisplayHelper.dp2px(getContext(), 50), QMUIDisplayHelper.dp2px(getContext(), 200), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), "camera " + i, Toast.LENGTH_SHORT).show();
                    mCamManager.switchCamera(String.valueOf(i), new CamManager.CameraInfoCallback() {
                        @Override
                        public void cameraFacing(int facing) {
                            mSeekBar.setVisibility(facing == CameraCharacteristics.LENS_FACING_FRONT ? View.INVISIBLE : View.VISIBLE);
                        }
                        @Override
                        public void cameraDeviceOnConfigured(CaptureRequest.Builder builder) { }
                    });
                    mListPopup.dismiss();
                }
            });
            mListPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }
    }

    public static CommonCap newInstance() {
        CommonCap fragment = new CommonCap();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCamManager = new CamManager(this, this.getActivity());
        mCamManager.checkCameraPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_common_cap, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shutter = view.findViewById(R.id.button_shutter);
        shutter.setOnClickListener(mButtonOnClickListener);
        switchCamera = view.findViewById(R.id.button_switchCamera);
        switchCamera.setOnClickListener(mButtonOnClickListener);
        mTextureView = view.findViewById(R.id.texture_commoncap);
        mSeekBar = view.findViewById(R.id.zoombar);
        mZoomLevel = view.findViewById(R.id.tv_zoomLevel);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    private void setZoomRatioText(float zoomzoomRatio) {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        mZoomLevel.setText(df.format(zoomzoomRatio) + "x");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSeekBar.setProgress(defaultProgress);
        if (mTextureView.isAvailable()) {
            mCamManager.openCamera(mTextureView);
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            mCamManager.openCamera(mTextureView);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    @Override
    public void onPause() {
        mCamManager.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
