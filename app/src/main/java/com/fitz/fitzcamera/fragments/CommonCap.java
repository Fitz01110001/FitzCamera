package com.fitz.fitzcamera.fragments;

import android.graphics.SurfaceTexture;
import android.media.ImageReader;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.fitz.fitzcamera.CamManager;
import com.fitz.fitzcamera.R;
import com.fitz.fitzcamera.ui.AutoFitTextureView;

import java.io.File;

import static android.os.Environment.DIRECTORY_DCIM;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommonCap.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommonCap#newInstance} factory method to
 * create an instance of this fragment.
 */
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
                    mCamManager.takeShot();
                    break;
                default:
                    break;
            }
        }
    };


    public CommonCap() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommonCap.
     */
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
        mCamManager = new CamManager(this.getActivity());
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
        setZoomLevel(mSeekBar.getProgress());
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "progress:" + progress);
                setZoomLevel(progress);
                mCamManager.setZoom(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setZoomLevel(int zoomLevel) {
        double level = (double) zoomLevel/10;
        mZoomLevel.setText(String.valueOf(level));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
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
