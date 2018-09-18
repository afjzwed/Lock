package com.cxwl.menjin.lock.face;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cxwl.menjin.lock.R;
import com.cxwl.menjin.lock.config.Constant;
import com.cxwl.menjin.lock.utils.DLLog;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;

/**
 * Created by William on 2018/7/30.
 */

public class PhotographActivity extends AppCompatActivity implements CameraSurfaceView.OnCameraListener, View
        .OnClickListener {

    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private ImageView mCaptureButton;
    private Camera mCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph2);

        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);
        mSurfaceView.debug_print_fps(true, false);

        mCaptureButton = (ImageView) findViewById(R.id.capture);//拍照按钮
        mCaptureButton.setOnClickListener(this);

        Constant.RESTART_AUDIO = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }).start();

    }

    @Override
    public Camera setupCamera() {
        mCamera = Camera.open(1);//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera
        if (null == mCamera) {
            mCamera = Camera.open(0);
        }
        if (null != mCamera) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(640, 480);
                parameters.setPreviewFormat(ImageFormat.NV21);
                mCamera.setParameters(parameters);
                mCamera.autoFocus(null);
            } catch (Exception e) {
                DLLog.e("AdvertiseHandler", "摄像头错误 setupCamera-->" + e.getMessage());
                Intent intent1 = new Intent(Intent.ACTION_REBOOT);
                intent1.putExtra("nowait", 1);
                intent1.putExtra("interval", 1);
                intent1.putExtra("window", 0);
                sendBroadcast(intent1);
                e.printStackTrace();
                Log.v("人脸识别", "setupCamera-->" + e.getMessage());
            }
        } else {
            DLLog.e("AdvertiseHandler", "摄像头错误 setupCamera-->PhotographActivity");
            Intent intent1 = new Intent(Intent.ACTION_REBOOT);
            intent1.putExtra("nowait", 1);
            intent1.putExtra("interval", 1);
            intent1.putExtra("window", 0);
            sendBroadcast(intent1);
        }
        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {

    }

    @Override
    public boolean startPreviewLater() {
        return false;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        return null;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {

    }

    @Override
    public void onClick(View view) {

    }
}
