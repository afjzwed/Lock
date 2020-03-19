package com.cxwl.menjin.lock.newface;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.cxwl.menjin.lock.config.Constant;
import com.cxwl.menjin.lock.config.DeviceConfig;
import com.cxwl.menjin.lock.entity.CompareResult;
import com.cxwl.menjin.lock.entity.FaceRegisterInfo;
import com.cxwl.menjin.lock.entity.FaceUrlBean;
import com.cxwl.menjin.lock.utils.DLLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 2020/3/19.
 */

public class FaceServer {
    private static final String TAG = "FaceServer";

    private static FaceEngine faceEngine = null;
    private static FaceServer faceServer = null;
    private static List<FaceRegisterInfo> faceRegisterInfoList;
    public static String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
    private String path;

    private boolean isProcessing = false;//是否正在搜索人脸，保证搜索操作单线程进行
    private byte[] mImageNV21 = null;//人脸图像数据

    public static FaceServer getInstance() {
        if (faceServer == null) {
            synchronized (FaceServer.class) {
                if (faceServer == null) {
                    faceServer = new FaceServer();
                }
            }
        }
        return faceServer;
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     * @return 是否初始化成功
     */
    public boolean init(Context context) {
        synchronized (this) {
            if (faceEngine == null && context != null) {
                faceEngine = new FaceEngine();
                int engineCode = faceEngine.init(context, DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority
                        .ASF_OP_0_ONLY, 16, 1, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
                if (engineCode == ErrorInfo.MOK) {
                    if (ROOT_PATH == null) {
                        ROOT_PATH = context.getFilesDir().getAbsolutePath();
                    }
                    path = ROOT_PATH + File.separator + DeviceConfig.NEW_LOCAL_FACEINFO_PATH;

                    initFaceList(context);
                    return true;
                } else {
                    faceEngine = null;
                    Log.e(TAG, "人脸初始化 init: failed! code = " + engineCode);
                    return false;
                }
            }
            return false;
        }
    }

    /**
     * 销毁
     */
    public void unInit() {
        synchronized (this) {
            if (faceRegisterInfoList != null) {
                faceRegisterInfoList.clear();
                faceRegisterInfoList = null;
            }
            if (faceEngine != null) {
                faceEngine.unInit();
                faceEngine = null;
            }
        }
    }

    /**
     * 初始化人脸特征数据以及人脸特征数据对应的注册图
     *
     * @param context 上下文对象
     */
    private void initFaceList(Context context) {
        synchronized (this) {
            Log.e(TAG, "path " + path);
            try {
                File file = new File(path);
                if (!file.exists()) {
                    new File(path).mkdirs();//新建文件夹
                    file.createNewFile();//新建文件
                }
            } catch (IOException e) {
                DLLog.e(TAG, "错误 e " + e.toString());
                e.printStackTrace();
            }

            File featureDir = new File(path);
            File[] featureFiles = featureDir.listFiles();
            if (featureFiles == null || featureFiles.length == 0) {
                Log.e(TAG, "人脸 newfaceinfo文件夹中为空");
                return;
            }
            Log.e(TAG, "人脸 myfaceinfo文件夹中有数据 " + featureFiles.length);
            faceRegisterInfoList = new ArrayList<>();
            for (File featureFile : featureFiles) {
                Log.e(TAG, "path " + featureFile.getAbsolutePath());
                try {
                    FileInputStream fis = new FileInputStream(featureFile);
                    byte[] feature = new byte[FaceFeature.FEATURE_SIZE];
                    fis.read(feature);
                    fis.close();
                    faceRegisterInfoList.add(new FaceRegisterInfo(feature, featureFile.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在特征库中搜索
     *
     * @param faceFeature 传入特征数据
     * @return 比对结果
     */
    public CompareResult getTopOfFaceLib(FaceFeature faceFeature) {
        if (faceEngine == null || isProcessing || faceFeature == null || faceRegisterInfoList == null ||
                faceRegisterInfoList.size() == 0) {
            return null;
        }
        FaceFeature tempFaceFeature = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();
        float maxSimilar = 0;
        String name = null;
        isProcessing = true;
        for (int i = 0; i < faceRegisterInfoList.size(); i++) {//与人脸库信息逐个对比,取最相似人脸
            tempFaceFeature.setFeatureData(faceRegisterInfoList.get(i).getFeatureData());
            //人脸特征比对，输出相似度(默认LIFE_PHOTO比对模型)
            //LIFE_PHOTO	用于生活照之间的特征比对
            //ID_CARD	用于证件照或生活照与证件照之间的特征比对
            faceEngine.compareFaceFeature(faceFeature, tempFaceFeature, faceSimilar);
            Log.e(TAG, "比对相似度 faceSimilar " + faceSimilar.getScore());
            // TODO: 2020/3/16 如果在这里设置阈值,超过阈值后直接跳出循环可以减少对比时间,但是会增加误识别率
            if (faceSimilar.getScore() > maxSimilar) {//此处未设阈值,在所有人脸比较完后,拿到最相似人脸
                maxSimilar = faceSimilar.getScore();
                name = faceRegisterInfoList.get(i).getName();
                if (maxSimilar > Constant.FACE_MAX) {
                    break;
                }
            }
        }
        isProcessing = false;
        if (name != null) {
            return new CompareResult(name, maxSimilar);
        }
        return null;
    }

    /**
     * 在人脸库中增加数据
     *
     * @param name
     * @param faceFeature
     * @return
     */
    public boolean addFace(String name, FaceFeature faceFeature) {
        Log.v("人脸识别", "addFace-->" + 111);
        boolean bool = false;
        try {
            Log.e(TAG, "人脸更新" + " restartFace faceUrlBean.getPath() " + path);
            //特征存储的文件夹
            File featureDir = new File(path);
            if (!featureDir.exists() && !featureDir.mkdirs()) {
                Log.e(TAG, "registerNv21: can not create feature directory");
            } else {
                FileOutputStream fosFeature = new FileOutputStream(featureDir + File.separator + name);
                fosFeature.write(faceFeature.getFeatureData());
                fosFeature.close();
                bool = true;
                Log.v("人脸识别", "addFace-->" + 222);
            }
        } catch (IOException e) {
            DLLog.e(TAG, "人脸添加失败 IOException " + e.getMessage());
            e.printStackTrace();
        }
        return bool;
    }

    public static List<FaceRegisterInfo> getFaceRegisterInfoList() {
        return faceRegisterInfoList;
    }

    public boolean addFace1(FaceUrlBean faceUrlBean){
        boolean bool = false;
        Log.e(TAG, "人脸更新 bin文件转换 " + faceUrlBean.toString());
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(faceUrlBean.getPath()));
            byte[] b = (byte[]) ois.readObject();
            ois.close();
//                Log.e("wh", "size " + b.length);
            if (null != b) {//b可能为空
                mImageNV21 = b.clone();
                FaceFeature faceFeature = new FaceFeature();
                faceFeature.setFeatureData(mImageNV21);
                Log.v("人脸识别", "addFace-->" + 111);

                //特征存储的文件夹
                File featureDir = new File(path);
                if (!featureDir.exists() && !featureDir.mkdirs()) {
                    Log.e(TAG, "registerNv21: can not create feature directory");
                } else {
                    FileOutputStream fosFeature = new FileOutputStream(featureDir + File.separator + faceUrlBean.getYezhuPhone());
                    fosFeature.write(faceFeature.getFeatureData());
                    fosFeature.close();
                    bool = true;
                    Log.v("人脸识别", "addFace-->" + 222);
                }
            }
            // TODO: 2019/11/28 这两个置null需要吗??
            b = null;
            mImageNV21 = null;
        } catch (IOException e) {
            DLLog.e(TAG, "错误 restartFace e " + e.toString());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            DLLog.e(TAG, "错误 restartFace e " + e.toString());
            e.printStackTrace();
        }
        return bool;
    }

    public boolean delete(String yezhuPhone) {
        try {
            //check if already registered.
            boolean find = false;
            for (FaceRegisterInfo frface : faceRegisterInfoList) {
                if (frface.getName().equals(yezhuPhone)) {
                    File delfile = new File(path + "/" + yezhuPhone);
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    faceRegisterInfoList.remove(frface);
                    find = true;
                    break;
                }
            }
            return find;
        } catch (Exception e) {
            Log.e(TAG, "删除人脸错误 " + e.toString());
            DLLog.e(TAG, "删除人脸错误 " + e.toString());
            e.printStackTrace();
        }
        return false;
    }
}
