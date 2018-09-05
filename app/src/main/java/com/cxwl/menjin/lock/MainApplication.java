package com.cxwl.menjin.lock;

import android.app.Application;
import android.app.PendingIntent;

import com.cxwl.menjin.lock.db.DaoMaster;
import com.cxwl.menjin.lock.db.DaoSession;
import com.cxwl.menjin.lock.face.ArcsoftManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by William on 2018/8/22.
 */

public class MainApplication  extends Application {

    private static MainApplication application;
    PendingIntent restartIntent;

    @Override
    public void onCreate() {

        application = this;
        //人脸识别的数据存本地
        ArcsoftManager.getInstance().initArcsoft(this);//虹软人脸识别初始化

        super.onCreate();

        //初始化腾讯buggly
        CrashReport.initCrashReport(this, "fd0b7bc576", true);

        //okhttp初始化
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new LoggerInterceptor("okhttp", true))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS).readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
//        Intent intent = new Intent();
//        // 参数1：包名，参数2：程序入口的activity
//        intent.setClassName("com.cxwl.hurry.doorlock", "com.cxwl.hurry.doorlock.ui.activity.MainActivity");
//        restartIntent = PendingIntent.getActivity(getApplicationContext(), 0,
//                intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程
    }

//    public Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
//        @Override
//        public void uncaughtException(Thread thread, Throwable ex) {
//            DLLog.e("崩溃重启", "错误 " + ex);
//            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
//                    restartIntent); // 1秒钟后重启应用
//            android.os.Process.killProcess(android.os.Process.myPid()); // 自定义方法，关闭当前打开的所有avtivity
//        }
//    };

    static DaoSession mDaoSessin;

    public static DaoSession getGreenDaoSession() {
        if (mDaoSessin == null) {
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(application, "door-db", null);
            DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
            mDaoSessin = daoMaster.newSession();
        }
        return mDaoSessin;
    }

    public static MainApplication getApplication() {
        return application;
    }
}
