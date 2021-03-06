package com.cxwl.menjin.lock.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by William on 2018/9/10.
 */

public class MonitorService extends Service {

    private String TAG = "";
    private Timer activityTimer;
    private ActivityManager activityManager;
    private Context mContext;
    private static final String PACKAGE_NAME = "com.cxwl.menjin.lock";
    private String Monitor_Broadcard = "com.androidex.lockaxial.MONITOR_ACTION";
    private boolean isPullTime = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
//                mContext.sendBroadcast(new Intent(Monitor_Broadcard));
//                mHandler.sendEmptyMessageDelayed(0x01, 1 * 1000);
            }
        }
    };

    private Runnable startMain = new Runnable() {
        @Override
        public void run() {
            try {
                Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MonitorService.this.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        Log.i("Monitor_", "监控服务初始化" + myFmt.format(new Date()));
        TAG = UUID.randomUUID().toString();
        mContext = this;
//        mHandler.sendEmptyMessage(0x01);
        initCheckTopActivity();
    }

    private void initCheckTopActivity() {
        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
        activityTimer = new Timer();
        activityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (activityManager == null) {
                    activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                }
                ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
                if (!cn.getPackageName().equals(PACKAGE_NAME)) {
                    showMsg("未打开锁相门禁" + isPullTime+" "+cn.getPackageName());
                    if (!isPullTime) {
                        showMsg("倒计时进入锁相门禁：15s");
                        mHandler.postDelayed(startMain, 15 * 1000);
                        isPullTime = true;
                    }
                } else {
                    showMsg("已经打开锁相门禁");
                    mHandler.removeCallbacks(startMain);
                    isPullTime = false;
                }
            }
        }, 0, 2 * 1000);
    }

    private void showMsg(String msg) {
        Log.i("Monitor_" + TAG, msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (activityTimer != null) {
            activityTimer.cancel();
            activityTimer = null;
        }
        mHandler.removeMessages(0x01);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
