package com.cxwl.menjin.lock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cxwl.menjin.lock.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static com.cxwl.menjin.lock.config.DeviceConfig.LOCAL_IMG_NAME;
import static com.cxwl.menjin.lock.config.DeviceConfig.LOCAL_VOICE_PATH;
import static com.cxwl.menjin.lock.config.DeviceConfig.isLocalPicHint;


/**
 * 门禁开锁的弹框
 * Created by William on 2018/5/17.
 */

public class DialogUtil {

    public Bitmap mBitmap;

    public static android.app.Dialog showBottomDialog(final Context context) {
        final android.app.Dialog dialog = new android.app.Dialog(context, R.style.DialogStyle);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_weituo, null);

        if (isLocalPicHint) {//在下载时用默认提示图片
            ImageView imageView = view.findViewById(R.id.iv_open);
            Bitmap bitmap = getLoacalBitmap(Environment.getExternalStorageDirectory() + "/" + LOCAL_VOICE_PATH + "/" +
                    LOCAL_IMG_NAME + ".png"); //从本地取图片(在cdcard中获取)  //
            imageView.setImageBitmap(bitmap); //设置Bitmap
        }

        dialog.setContentView(view);

        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            lp.height = getScreenHeight(context) * 1 / 2;
        } else {
            lp.width = getScreenWidth(context) * 1 / 2;
        }
        mWindow.setGravity(Gravity.CENTER);
        //mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);

        Log.e("DialogUtil", "读取图片 这里");
//        dialog.show();
        return dialog;
    }

    /**
     * 获取屏幕分辨率宽
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕分辨率高
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        Log.e("DialogUtil", "读取图片");
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
