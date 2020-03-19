package com.cxwl.menjin.lock.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.arcsoft.face.ActiveFileInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.RuntimeABI;
import com.cxwl.menjin.lock.MainApplication;
import com.cxwl.menjin.lock.R;
import com.cxwl.menjin.lock.config.Constant;
import com.cxwl.menjin.lock.newface.FaceServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by William on 2020/3/18.
 */

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "SplashActivity";

    private ImageView imageView;

    private boolean libraryExists = true;
    // Demo 所需的动态库文件
    private static final String[] LIBRARIES = new String[]{
            // 人脸相关
            "libarcsoft_face_engine.so",
            "libarcsoft_face.so",
            // 图像库相关
            "libarcsoft_image_util.so",
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = (ImageView) findViewById(R.id.image_view);
        imageView.setOnClickListener(this);


        initFaceTimer();//延时开关灯定时器初始化
        libraryExists = checkSoFile(LIBRARIES);
        ApplicationInfo applicationInfo = getApplicationInfo();
        Log.i(TAG, "onCreate: " + applicationInfo.nativeLibraryDir);
        if (!libraryExists) {
            Log.e(TAG, "未找到库文件，请检查是否有将.so文件放至工程的 app\\\\src\\\\main\\\\jniLibs 目录下");
        } else {
            activeEngine(null);
        }
    }

    /**
     * 激活引擎
     *
     * @param view
     */
    public void activeEngine(final View view) {
        if (!libraryExists) {
            Log.e(TAG, "未找到库文件，请检查是否有将.so文件放至工程的 app\\\\src\\\\main\\\\jniLibs 目录下");
            return;
        }
        if (view != null) {
            view.setClickable(false);
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) {
                RuntimeABI runtimeABI = FaceEngine.getRuntimeABI();//获取运行时架构(无需激活或初始化即可调用)
                Log.i(TAG, "subscribe: getRuntimeABI() " + runtimeABI);
                //初次使用SDK时需要对SDK先进行激活，激活后无需重复调用;调用此接口时必须为联网状态，激活成功后即可离线使用
                int activeCode = FaceEngine.activeOnline(MainApplication.getApplication(), Constant.arc_app_id, Constant.arc_sdk_key);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            Log.e(TAG, "激活引擎成功");
                            timer.start();
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            Log.e(TAG, "引擎已激活，无需再次激活");
                            timer.start();
                        } else {
                            Log.e(TAG, "引擎激活失败");//引擎激活失败
                        }

                        if (view != null) {
                            view.setClickable(true);
                        }
                        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                        //获取激活文件信息
                        int res = FaceEngine.getActiveFileInfo(SplashActivity.this, activeFileInfo);
                        if (res == ErrorInfo.MOK) {
                            Log.i(TAG, activeFileInfo.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError " + e.getMessage());
                        if (view != null) {
                            view.setClickable(true);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });

    }

    /**
     * 检查能否找到动态链接库，如果找不到，请修改工程配置
     *
     * @param libraries 需要的动态链接库
     * @return 动态库是否存在
     */
    private boolean checkSoFile(String[] libraries) {
        File dir = new File(getApplicationInfo().nativeLibraryDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        List<String> libraryNameList = new ArrayList<>();
        for (File file : files) {
            libraryNameList.add(file.getName());
        }
        boolean exists = true;
        for (String library : libraries) {
            exists &= libraryNameList.contains(library);
        }
        return exists;
    }

    private CountDownTimer timer;
    private CountDownTimer timer1;

    private void initFaceTimer() {

        timer = new CountDownTimer(10000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.i(TAG, "-------------------剩余" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                //开子线程加载人脸
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "初始化引擎 开始加载人脸");
                        //本地人脸库初始化
                        FaceServer.getInstance().init(MainApplication.getApplication());
                        Log.e(TAG, "结束加载人脸");
                        Log.e(TAG, "开始定时");
                        timer1.start();
                    }
                }).start();
            }
        };

        timer1 = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.i(TAG, "-------------------剩余" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Intent intent1 = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "取消定时器");
        if (timer1 != null) {
            timer1.cancel();
            timer1 = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
