package com.cxwl.menjin.lock.utils;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2018/6/13.
 */

public class CameraHelperDex implements Camera.PreviewCallback {
    private Activity mActivity;
    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private SurfaceHolder mSurfaceHolder;
    private int mCameraFacing = 0;
    private int mDisplayOrientation = 0;
    private int picWidth = 640;
    private int picHeight = 480;
    private int privWidth = 640;
    private int privHeight = 480;
    private CallBack mCallBack;

    public CameraHelperDex(Activity activity, SurfaceView surfaceView) {
        this.mActivity = activity;
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolder = mSurfaceView.getHolder();
        init();
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        //width * height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8]结果为NV21格式的图片size
        if (bytes == null) {
            bytes = new byte[((privWidth * privHeight) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8];
        }
        mCamera.addCallbackBuffer(bytes);
        if (this.mCallBack != null) {
            this.mCallBack.onPreviewFrame(bytes);
        }

    }

    public void takePic() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    if (mCallBack != null) {
                        mCallBack.onTakePic(bytes);
                    }

                }
            });
        }
    }

    public int getCameradirection() {
        return mCameraFacing;
    }

    public int getDisplayOrientation() {
        return mDisplayOrientation;
    }

    private void init() {
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (mCamera == null) {
                    openCamera(mCameraFacing);
                }
                startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                releaseCamera();
            }
        });
    }

    private boolean openCamera(int cameraFacing) {
        boolean supportCameraFacing = true; //supportCameraFacing(cameraFacing);
        if (supportCameraFacing) {
            try {
                mCamera = Camera.open(cameraFacing);
                initParameters(mCamera);
                mCamera.addCallbackBuffer(new byte[((privWidth * privHeight) * ImageFormat.getBitsPerPixel
                        (ImageFormat.NV21)) / 8]);//这就代码可以多调用两次,相当于多加了两块缓冲区域
                //这个方法会使回调内每一帧的data就会复用同一块缓冲区域，data对象没有改变，但是data数据的内容改变了，并且该回调不会返回每一帧的数据，而是在重新调用 addCallbackBuffer
                // 之后才会继续回调，这样我们可以更容易控制回调的数量
                mCamera.setPreviewCallbackWithBuffer(this);//更好的性能, 更容易控制的方式,配合mCamera.addCallbackBuffer(new
                // byte[size])方法使用
                //mCamera.setPreviewCallback(this);
                // 这个方法不推荐使用，因为该回调会将每一帧数据一个不漏的给你，大多数情况下我们根本来不及处理，会将帧数据直接丢弃。另外每一帧的数据都是一块新的内存区域会造成频繁的 GC
                Log.e("wh", "cameraFacing " + cameraFacing + " 打开相机成功!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("wh", "cameraFacing " + cameraFacing + " 打开相机失败!" + e.toString());
                toast("打开相机失败!");
                return false;
            }
        }
        return supportCameraFacing;
    }

    private void initParameters(Camera camera) {
        mParameters = camera.getParameters();
        //mParameters.setPreviewFormat(ImageFormat.NV21);
        setPreviewFormat(); //设置NV21
        //Camera.Size privSize = getBestSize(mSurfaceView.getWidth(),mSurfaceView.getHeight(),mParameters
        // .getSupportedPreviewSizes());
        //mParameters.setPreviewSize(privSize.width, privSize.height);
        setPreviewSizes(); //设置预览分辨率
        //Camera.Size picSize = getBestSize(picWidth, picHeight, mParameters.getSupportedPictureSizes());
        //mParameters.setPictureSize(picSize.width, picSize.height);

//        auto  //自动
//        infinity //无穷远
//        macro //微距
//        continuous-picture //持续对焦
//        fixed //固定焦距
        if (isSupportFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            log("不支持自动连续对焦");
        }
        camera.setParameters(mParameters);
    }

    public int getPrivWidth() {
        return privWidth;
    }

    public int getPrivHeight() {
        return privHeight;
    }

    private void setPreviewSizes() {
        try {
            if (mParameters != null && mSurfaceView != null) {
                Camera.Size privSize = getBestSize(640, 480, mParameters.getSupportedPreviewSizes());
                privWidth = privSize.width;
                privHeight = privSize.height;
                mParameters.setPreviewSize(privSize.width, privSize.height);
                log("设置预览宽高完成");
            }
        } catch (Exception e) {
            log("设置预览宽高失败");
            e.printStackTrace();
        }
    }

    private void setPreviewFormat() {
        try {
            if (mParameters != null) {
                mParameters.setPreviewFormat(ImageFormat.NV21);
                mCamera.setParameters(mParameters);
                log("设置NV21完成");
            }
        } catch (Exception e) {
            log("设置NV21失败");
            e.printStackTrace();
        }
    }

    private void startPreview() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                setCameraDisplayOrientation(mActivity);
                mCamera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isSupportFocus(String focusMode) {
        boolean autoFocus = false;
        List<String> listFocusMode = mParameters.getSupportedFocusModes();
        for (String mode : listFocusMode) {
            log("对焦模式：" + mode);
            if (mode.equals(focusMode)) {
                autoFocus = true;
            }
        }
        return autoFocus;
    }

    public void exchangeCamera() {
        releaseCamera();
        mCameraFacing = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;
        openCamera(mCameraFacing);
        startPreview();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Size getBestSize(int width, int height, List<Camera.Size> list) {
        Camera.Size size = null;
        double targetRatio = height * 1.0 / width * 1.0;
        double minDiff = targetRatio;

        for (Camera.Size cSize : list) {
            double supportedRatio = ((cSize.width * 1.0) / cSize.height);
            log("系统支持的尺寸 : " + cSize.width + " * " + cSize.height + "比例" + supportedRatio);
        }

        for (Camera.Size cSize : list) {
            if (cSize.width == width && cSize.height == height) {
                size = cSize;
                break;
            }
            double ratio = (cSize.width * 1.0) / cSize.height;
            if (Math.abs(ratio - targetRatio) < minDiff) {
                minDiff = Math.abs(ratio - targetRatio);
                size = cSize;
            }
        }
        log("目标尺寸 ：" + width + " * " + height + "   比例:" + targetRatio);
        log("最优尺寸 ：" + size.width + " * " + size.height);
        return size;
    }

    private void setCameraDisplayOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraFacing, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int screenDegree = 0;
        switch (rotation) {
            case Surface.ROTATION_0: {
                screenDegree = 0;
            }
            break;
            case Surface.ROTATION_90: {
                screenDegree = 90;
            }
            break;
            case Surface.ROTATION_180: {
                screenDegree = 180;
            }
            break;
            case Surface.ROTATION_270: {
                screenDegree = 270;
            }
            break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDisplayOrientation = (info.orientation + screenDegree) % 360;
            mDisplayOrientation = (360 - mDisplayOrientation) % 360;
        } else {
            mDisplayOrientation = (info.orientation - screenDegree + 360) % 360;
        }
        Log.i("wh", "mDisplayOrientation = " + mDisplayOrientation);
        mCamera.setDisplayOrientation(mDisplayOrientation);
    }

    private boolean supportCameraFacing(int cameraFacing) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int n = Camera.getNumberOfCameras();
        for (int i = 0; i < n; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraFacing) {
                return true;
            }
        }
        return false;
    }

    private void toast(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    private void log(String msg) {
        Log.i("xiao_", msg);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void addCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }

    public interface CallBack {
        public void onPreviewFrame(byte[] bytes);

        public void onTakePic(byte[] bytes);
    }
}
