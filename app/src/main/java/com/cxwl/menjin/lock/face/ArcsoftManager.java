package com.cxwl.menjin.lock.face;

import android.app.Application;
import android.os.Environment;
import android.util.Log;


import com.cxwl.menjin.lock.config.DeviceConfig;
import com.cxwl.menjin.lock.utils.DLLog;
import com.cxwl.menjin.lock.utils.SharedPreferencesUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by William on 2018/5/17.
 */

public class ArcsoftManager {

    // Singleton instance
    private static final ArcsoftManager INSTANCE = new ArcsoftManager();

    /**
     * get singleton instance of CallManager
     *
     * @return CallManager
     */
    public static ArcsoftManager getInstance() {
        return INSTANCE;
    }

    private ArcsoftManager() {
    }

    public FaceDB mFaceDB;
//    public Uri mImage;

    public void initArcsoft(Application application) {
//        String path = application.getExternalCacheDir().getPath();
//        String path = Environment.getExternalStorageDirectory()+"";
        String path = Environment.getExternalStorageDirectory() + File.separator + DeviceConfig.LOCAL_FACEINFO_PATH;
        try {
            File file = new File(path);
            if (!file.exists()) {
                new File(path).mkdirs();//新建文件夹
                file.createNewFile();//新建文件
            }
        } catch (IOException e) {
//            path = application.getExternalCacheDir().getPath();
            DLLog.e("ArcsoftManager", "错误 e " + e.toString());
            e.printStackTrace();
        }

        Log.v("人脸识别", "initArcsoft-->" + path);
        mFaceDB = new FaceDB(path);
//        mImage = null;

        SharedPreferencesUtil.getInstance(application, "share_dysmart");
    }

//    public void setCaptureImage(Uri uri) {
//        mImage = uri;
//    }
//
//    public Uri getCaptureImage() {
//        return mImage;
//    }

    public void saveIDCardData(String id) {
        List<String> list = getIDCardData();
        if (list == null) {
            list = new ArrayList<>();
        }
        if (!list.contains(id)) {
            list.add(id);
            SharedPreferencesUtil.putListData("ID_datas", list);
        }
    }

    public List<String> getIDCardData() {
        List<String> list = SharedPreferencesUtil.getListData("ID_datas", String.class);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public void saveIDCardData(String name, String id) {
        HashMap<String, List<String>> faceMap = (HashMap<String, List<String>>) SharedPreferencesUtil.getMap("ID_data");
        if (faceMap == null) {
            faceMap = new HashMap<>();
        }
        List<String> list = faceMap.get(name);
        if (list == null) {
            list = new ArrayList<>();
        }
        if (!list.contains(id)) {
            list.add(id);
            faceMap.put(name, list);
            SharedPreferencesUtil.putMap("ID_data", faceMap);
        }
    }

    public List<String> getIDCardData(String name) {
        HashMap<String, List<String>> faceMap = (HashMap<String, List<String>>) SharedPreferencesUtil.getMap("ID_data");
        if (faceMap == null) {
            faceMap = new HashMap<>();
        }
        List<String> list = faceMap.get(name);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    //将list转换为带有 ， 的字符串
    private String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    //将list转换为带有 ， 的字符串
    private List<String> stringToList(String str) {
        String[] arr = str.split(",");
        return Arrays.asList(arr);
    }
}
