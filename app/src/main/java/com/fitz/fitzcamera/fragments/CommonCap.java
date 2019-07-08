package com.fitz.fitzcamera.fragments;

import android.graphics.SurfaceTexture;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fitz.fitzcamera.CamManager;
import com.fitz.fitzcamera.R;
import com.fitz.fitzcamera.ui.AutoFitTextureView;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class CommonCap extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CommonCap";

    // TODO: Rename and change types of parameters
/*    private String mParam1;
    private String mParam2;*/
    private CamManager mCamManager;

    private final int defaultProgress = 10;

    /**
     * 拍照用的 buttn
     */
    private Button shutter;
    /**
     * 预览显示view
     */
    private AutoFitTextureView mTextureView;

    private SeekBar mSeekBar;

    private TextView mZoomLevel;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_shutter:
                    Log.d(TAG, "点击拍照");
                    String imageUri = mCamManager.takeShot();
                    Toast.makeText(CommonCap.this.getActivity(),imageUri,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    public CommonCap() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    /* public static CommonCap newInstance(String param1, String param2) {
        CommonCap fragment = new CommonCap();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    public static CommonCap newInstance() {
        CommonCap fragment = new CommonCap();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
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
        shutter.setOnClickListener(onClickListener);
        mTextureView = view.findViewById(R.id.texture_commoncap);
        mSeekBar = view.findViewById(R.id.zoombar);
        mZoomLevel = view.findViewById(R.id.tv_zoomLevel);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "progress:" + progress);
                float zoomRatio = (float) progress / 1000;
                setZoomRatioText(zoomRatio);
                mCamManager.setZoomRatio(zoomRatio);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setZoomRatioText(float zoomzoomRatio) {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        mZoomLevel.setText(df.format(zoomzoomRatio)+"x");
    }

    public float getZoomRatio() {
        return (float) mSeekBar.getProgress() / 10;
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
            mCamManager.openCamera(mTextureView, mTextureView.getWidth(), mTextureView.getHeight());
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
            mCamManager.openCamera(mTextureView, mTextureView.getWidth(), mTextureView.getHeight());
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
