package com.fitz.fitzcamera.fragments;

import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fitz.fitzcamera.CamManager;
import com.fitz.fitzcamera.R;
import com.fitz.fitzcamera.ui.AutoFitTextureView;
import com.fitz.fitzcamera.ui.ShutterTouchListener;
import com.fitz.fitzcamera.ui.TextureViewTouchListener;
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

    /**
     * 默认显示的zoom值，为1
     * */
    private final int defaultZoom = 100;

    /**
     * 通过快门键左右滑动zoom，步进值要小
     * */
    private final float dZoom = 0.02f;

    /**
     * 拍照用的 buttn
     */
    private ImageButton shutter;

    /**
     * 切换镜头
     * */
    private ImageButton switchCamera;

    /**
     * 滑动快门键时显示的缩放条
     * */
    private LinearLayout zoombar;

    /**
     * 预览显示view
     */
    private AutoFitTextureView mTextureView;

    /**
     * 显示当前zoom值
     * */
    private TextView mZoomLevel;

    /**
     * 切换镜头时弹出的menu
     * */
    private QMUIListPopup mListPopup;

    private View.OnClickListener mButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_shutter:
                    Log.d(TAG, "点击拍照");
                    //mCamManager.onPause();
                    String imageUri = mCamManager.takeShot();
                    Toast.makeText(CommonCap.this.getActivity(), imageUri, Toast.LENGTH_SHORT).show();
                    //mCamManager.getLastFrame(mTextureView.getBitmap());
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

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable");
            mCamManager.openCamera(mTextureView);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
            Log.d(TAG, "onSurfaceTextureUpdated");

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
                        }

                        @Override
                        public void cameraDeviceOnConfigured(CaptureRequest.Builder builder) {
                        }
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
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mCamManager = new CamManager(this, this.getActivity());
        mCamManager.checkCameraPermission();
        mCamManager.getDeviceInfo();

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
        zoombar = view.findViewById(R.id.zoombar_horizontal);
        switchCamera = view.findViewById(R.id.button_switchCamera);
        switchCamera.setOnClickListener(mButtonOnClickListener);
        shutter.setOnTouchListener(new ShutterTouchListener(this.getActivity(), new ShutterTouchListener.TouchCallBack() {
            @Override
            public void onTouchMove(int dx, int diffX) {
                switchCamera.setVisibility(View.GONE);
                zoombar.setVisibility(View.VISIBLE);
                if (diffX > 0) {
                    mCamManager.zoomIn(dZoom);
                } else if (diffX < 0) {
                    mCamManager.zoomOut(dZoom);
                }
            }

            @Override
            public void onTouchUP() {
                switchCamera.setVisibility(View.VISIBLE);
                zoombar.setVisibility(View.GONE);
            }
        }));
        mTextureView = view.findViewById(R.id.texture_commoncap);
        mTextureView.setOnTouchListener(new TextureViewTouchListener(mCamManager));
        mZoomLevel = view.findViewById(R.id.tv_zoomLevel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setZoomRatioText((int) defaultZoom / 100);
        if (mTextureView.isAvailable()) {
            mCamManager.openCamera(mTextureView);
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    /**
     * 更新 zoom 显示
     * */
    public void updateZoomRatio(float zoomRatio) {
        setZoomRatioText(zoomRatio);
    }


    /**
     * 设置当前的zoom值，保留一位小数
     * */
    private void setZoomRatioText(float zoomRatio) {
        if (zoomRatio < 1f) {
            mZoomLevel.setText("WIDE");
        } else {
            DecimalFormat df = new DecimalFormat("0.0");
            df.setRoundingMode(RoundingMode.HALF_UP);
            mZoomLevel.setText(df.format(zoomRatio) + "x");
        }
    }

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
