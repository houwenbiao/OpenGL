package com.jackhou.opengl.textureview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

/**
 * Author: JackHou
 * Date: 2020/5/7.
 */
public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private static final String TAG = "JackHou";
    Context mContext;
    SurfaceTexture mSurfaceTexture;
    private Camera mCamera;

    public CameraTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.setSurfaceTextureListener(this);
        Log.i(TAG, "CameraTextureView");
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable");
        mSurfaceTexture = surface;
        openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i(TAG, "onSurfaceTextureUpdated");
    }


    private void openCamera() {
        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, camInfo);
            if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    mCamera = Camera.open(i);
                    mCamera.setErrorCallback(new Camera.ErrorCallback() {
                        @Override
                        public void onError(int error, Camera camera) {
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                break;
            }
        }
        if (mCamera == null) {
            return;
        }
        Camera.Parameters camParams = mCamera.getParameters();
        List<Camera.Size> sizes = camParams.getSupportedPreviewSizes();
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);
            Log.v(TAG, "Camera Supported Preview Size = " + size.width + "x" + size.height);
        }
        camParams.setPreviewSize(1280, 720);
        camParams.setRecordingHint(true);
        mCamera.setParameters(camParams);
        if (mSurfaceTexture != null) {
            try {
                mCamera.setPreviewTexture(mSurfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.startPreview();
        }
    }
}
