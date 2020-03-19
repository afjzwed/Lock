package com.cxwl.menjin.lock.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.cxwl.menjin.lock.ui.MainActivity;
import com.cxwl.menjin.lock.MainApplication;
import com.cxwl.menjin.lock.config.DeviceConfig;
import com.cxwl.menjin.lock.ui.SplashActivity;


/**
 * 开机(更新)启动广播监听器
 * Created by William on 2018/4/24.
 */

public class NativeAccessReceiver extends BroadcastReceiver {

    private static final String Lockaxial_PackageName = "com.cxwl.menjin.lock";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_PACKAGE_REPLACED) || action.equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (packageName.equals(Lockaxial_PackageName)) {
                startActivity(context, SplashActivity.class, null);
            }else if(packageName.equals(DeviceConfig.Lockaxial_Monitor_PackageName)){
                //启动监控程序
                Intent i = new Intent();
                ComponentName cn = new ComponentName(DeviceConfig.Lockaxial_Monitor_PackageName,DeviceConfig.Lockaxial_Monitor_SERVICE);
                i.setComponent(cn);
                i.setPackage(MainApplication.getApplication().getPackageName());
                context.startService(i);
            }
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            startActivity(context, MainActivity.class,null);
//            ToastUtil.showShort("开机启动成功");
        }

    }


    public void startActivity(Context context,Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
