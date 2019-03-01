package com.cxwl.menjin.lock;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cxwl.menjin.lock.db.DaoMaster;
import com.cxwl.menjin.lock.db.DaoSession;
import com.cxwl.menjin.lock.face.ArcsoftManager;
import com.cxwl.menjin.lock.utils.DLLog;
import com.squareup.leakcanary.LeakCanary;
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

//        LeakCanary.install(this);

        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程

        Intent intent = new Intent();
        // 参数1：包名，参数2：程序入口的activity
        intent.setClassName("com.cxwl.menjin.lock", "com.cxwl.menjin.lock.ui.MainActivity");
        restartIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

//            StringBuffer sb = new StringBuffer();
//            Log.e("崩溃重启", "错误 普通 " + ex);
//            Log.e("崩溃重启", "错误 getMessage " + ex.getMessage());
//            Log.e("崩溃重启", "错误 getLocalizedMessage " + ex.getLocalizedMessage());
//            Log.e("崩溃重启", "错误 getCause " + ex.getCause());
//            Log.e("崩溃重启", "错误 toString " + ex.toString());
//            StackTraceElement[] stackTrace = ex.getStackTrace();// 提供编程访问由 printStackTrace() 输出的堆栈跟踪信息。
//            for (int i = 0;i<stackTrace.length;i++) {
//            for (StackTraceElement traceElement : stackTrace) {
//
//                sb.append("class: ").append(traceElement.getClassName())
//                                    .append("; method: ").append(traceElement.getMethodName())
//                                     .append("; line: ").append(traceElement.getLineNumber())
//                                    .append(";  Exception: ").append(ex.toString() + "\n");
//            }
//            Log.e("崩溃重启", "错误 StackTraceElement " + sb);
//            ex.printStackTrace();


            DLLog.e("崩溃重启", "错误 " + ex);
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    restartIntent); // 1秒钟后重启应用
            android.os.Process.killProcess(android.os.Process.myPid()); // 自定义方法，关闭当前打开的所有avtivity
            System.exit(0);
            // TODO: 2018/9/26 测试是否要加System.exit(0);才能在每次成功重启
//            System.gc();
        }
    };

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
