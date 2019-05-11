package com.cxwl.menjin.lock.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.cxwl.menjin.lock.MainApplication;
import com.cxwl.menjin.lock.config.Constant;
import com.cxwl.menjin.lock.entity.FaceRegist;
import com.cxwl.menjin.lock.utils.BitmapUtils;
import com.cxwl.menjin.lock.utils.DLLog;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by William on 2018/5/17.
 */

public class FaceDB {
    private final String TAG = this.getClass().toString();


    String mDBPath;
    public List<FaceRegist> mRegister;//已注册的集合（用text文件保存）
    AFR_FSDKEngine mFREngine;
    AFR_FSDKVersion mFRVersion;
    boolean mUpgrade;

    public FaceDB(String path) {
        mDBPath = path;
        mRegister = new ArrayList<>();
        mFRVersion = new AFR_FSDKVersion();
        mUpgrade = false;
        mFREngine = new AFR_FSDKEngine();
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(Constant.arc_appid, Constant.fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
            DLLog.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
            Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString() + " error " + error.getCode());
//            DLLog.e(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString() + " error " + error.getCode());
        }

        initFaceList(MainApplication.getApplication());
    }

    public void destroy() {
        if (mFREngine != null) {
            mFREngine.AFR_FSDK_UninitialEngine();
        }
    }

    private boolean saveInfo() {
        try {
            FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
            ExtOutputStream bos = new ExtOutputStream(fs);
            bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadInfo() {
        if (!mRegister.isEmpty()) {
            return false;
        }
        try {
            FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
            ExtInputStream bos = new ExtInputStream(fs);
            //load version
            String version_saved = bos.readString();
            if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
                mUpgrade = true;
            }
            //load all regist name.
            if (version_saved != null) {
                for (String name = bos.readString(); name != null; name = bos.readString()) {
                    if (new File(mDBPath + "/" + name + ".data").exists()) {
//                        Log.v("人脸识别", "loadInfo-->" + name);
                        mRegister.add(new FaceRegist(new String(name)));
                    }
                }
            }
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("人脸识别", "loadInfo/FileNotFoundException-->" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("人脸识别", "loadInfo/IOException-->" + e.getMessage());
        }
        return false;
    }

    public boolean saveBitmap(String name, Bitmap bitmap) {
        boolean bool = false;
        File file = new File(mDBPath + "/" + name + ".png");
        Log.v("人脸识别", "saveBitmap1-->" + file.getPath());
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            Bitmap bmp = BitmapUtils.zoomImg(bitmap, 80, 80);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            bool = true;
            Log.v("人脸识别", "saveBitmap2-->" + file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("人脸识别", "saveBitmap3-->" + e.getMessage());
            bool = false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("人脸识别", "saveBitmap4-->" + e.getMessage());
            bool = false;
        }
        return bool;
    }

    public Bitmap getFaceBitmap(String name) {
        Bitmap bitmap = null;
        try {
            File file = new File(mDBPath + "/" + name + ".png");
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getPath());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public boolean loadFaces() {
        if (loadInfo()) {
            try {
                for (FaceRegist face : mRegister) {
//                    Log.v("人脸识别", "load name:" + face.mName + "'s face feature data.");
                    FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
                    ExtInputStream bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            if (mUpgrade) {
                                //upgrade data.
                            }
                            if (face.mName.length() > 11) {
                                face.mIDFaceList.add(afr);
                            } else {
                                face.mFaceList.add(afr);
                            }
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    bos.close();
                    fs.close();
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                DLLog.e(TAG, "loadFaces/FileNotFoundException-->" + e.getMessage());
                Log.v("人脸识别", "loadFaces/FileNotFoundException-->" + e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
                DLLog.e(TAG, "loadFaces/IOException-->" + e.getMessage());
                Log.v("人脸识别", "loadFaces/IOException-->" + e.getMessage());
            }
        }
        return false;
    }

    public boolean addFace(String name, AFR_FSDKFace face) {

        boolean bool = false;
        try {
            //check if already registered.
            boolean add = true;
            for (FaceRegist frface : mRegister) {//遍历已有数据库，检测是否注册过手机号
                if (frface.mName.equals(name)) {//数据库中已有对应手机号，直接添加数据至对应集合中
                    if (name.length() > 11) {
                        frface.mIDFaceList.add(face);//大于11位录入身份证号
                    } else {
                        frface.mFaceList.add(face);//11位以下录入手机号
                    }
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.手机号未注册
                FaceRegist frface = new FaceRegist(name);
                if (name.length() > 11) {
                    frface.mIDFaceList.add(face);
                } else {
                    frface.mFaceList.add(face);
                }
                mRegister.add(frface);
                Log.v("人脸识别", "addFace-->" + 333333);
            }

            Log.v("人脸识别", "addFace-->" + 111);
            bool = saveInfo();//保存数据
            if (bool) {
                //update all names
                FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                ExtOutputStream bos = new ExtOutputStream(fs);
                for (FaceRegist frface : mRegister) {
                    bos.writeString(frface.mName);
                }
                bos.close();
                fs.close();

                //save new feature 单独保存人脸数据
                fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
                bos = new ExtOutputStream(fs);
                bos.writeBytes(face.getFeatureData());
                bos.close();
                fs.close();

                bool = true;
                Log.v("人脸识别", "addFace-->" + 222);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bool = false;
        } catch (IOException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }

    public boolean delete(String name) {
        try {
            //check if already registered.
            boolean find = false;
            for (FaceRegist frface : mRegister) {
                if (frface.mName.equals(name)) {
                    File delfile = new File(mDBPath + "/" + name + ".data");
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    mRegister.remove(frface);
                    find = true;
                    break;
                }
            }

            if (find) {
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.mName);
                    }
                    bos.close();
                    fs.close();
                }
            }
            return find;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean upgrade() {
        return false;
    }


    //    private static List<FaceRegisterInfo> faceRegisterInfoList;
    public List<FaceRegist> mRegister1;//已注册的集合（用text文件保存）
    public static String ROOT_PATH;
    private static final String SAVE_FEATURE_DIR = "register" + File.separator + "features";

    /**
     * 初始化人脸特征数据以及人脸特征数据对应的注册图
     *
     * @param context 上下文对象
     */
    private void initFaceList(Context context) {
        mRegister1 = new ArrayList<>();
        // TODO: 2019/3/15 这里
        synchronized (this) {
//            if (ROOT_PATH == null) {
//                ROOT_PATH = context.getFilesDir().getAbsolutePath();
//            }
//            Log.e(TAG, "人脸 " + "ROOT_PATH " + ROOT_PATH);
            Log.e(TAG, "人脸 " + "mDBPath " + mDBPath);
            File featureDir = new File(mDBPath);
//            if (!featureDir.exists() || !featureDir.isDirectory()) {
//                return;
//            }
            File[] featureFiles = featureDir.listFiles();
            if (featureFiles == null || featureFiles.length == 0) {
                Log.e(TAG, "人脸 myfaceinfo文件夹中为空");
                return;
            }

            Log.e(TAG, "人脸 myfaceinfo文件夹中有数据 " + featureFiles.length);

            for (int i = 0; i < featureFiles.length; i++) {
                try {
                    File featureFile = featureFiles[i];
                    if (featureFile.getName().contains(".data")) {
//                        Log.e(TAG, "人脸 myfaceinfo文件夹中有数据  getAbsolutePath  " + featureFile.getAbsolutePath());
                        //mnt/internal_sd/myfaceinfo/15200350491.data
//                        Log.e(TAG, "人脸 myfaceinfo文件夹中有数据  getPath  " + featureFile.getPath());
//                        Log.e(TAG, "人脸 myfaceinfo文件夹中有数据  getName  " + featureFile.getName());//15211067316.data
//                        FileInputStream fs = new FileInputStream(featureFile);
                        FileInputStream fs = new FileInputStream(featureFile.getAbsolutePath());

                        ExtInputStream bos = new ExtInputStream(fs);
                        AFR_FSDKFace afr = null;
                        String str = featureFile.getName();
                        String name = str.substring(0, str.indexOf("."));
                        FaceRegist face = new FaceRegist(name);
                        do {
                            if (afr != null) {
                                face.mFaceList.add(afr);
                            }
                            afr = new AFR_FSDKFace();
                        } while (bos.readBytes(afr.getFeatureData()));
                        if (face.mFaceList != null && face.mFaceList.size() > 0) {
                            mRegister1.add(face);
                        }
                        bos.close();
                        fs.close();
                    }
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "人脸 没有发现文件 " + e.toString());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG, "人脸 IOException " + e.toString());
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "人脸 mRegister1 " + mRegister1.size());
        }
    }

    public boolean delete1(String name) {
        try {
            //check if already registered.
            boolean find = false;
            for (FaceRegist frface : mRegister1) {
                if (frface.mName.equals(name)) {
                    File delfile = new File(mDBPath + "/" + name + ".data");
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    mRegister1.remove(frface);
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
//
// catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public boolean addFace1(String name, AFR_FSDKFace face) {

        boolean bool = false;
        try {
            //check if already registered.
            boolean add = true;
            for (FaceRegist frface : mRegister1) {//遍历已有数据库，检测是否注册过手机号
                if (frface.mName.equals(name)) {//数据库中已有对应手机号，直接添加数据至对应集合中
                    if (name.length() > 11) {
                        frface.mIDFaceList.add(face);//大于11位录入身份证号
                    } else {
                        frface.mFaceList.add(face);//11位以下录入手机号
                    }
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.手机号未注册
                FaceRegist frface = new FaceRegist(name);
                frface.mFaceList.add(face);
                mRegister1.add(frface);
                Log.v("人脸识别", "addFace-->" + 333333);
            }

            Log.v("人脸识别", "addFace-->" + 111);
//            bool = saveInfo();//保存数据
            bool = true;//保存数据
            if (bool) {
                //save new feature 单独保存人脸数据
                FileOutputStream fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
                ExtOutputStream bos = new ExtOutputStream(fs);
                bos.writeBytes(face.getFeatureData());
                bos.close();
                fs.close();

                bool = true;
                Log.v("人脸识别", "addFace-->" + 222);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "人脸添加失败 FileNotFoundException ");
            DLLog.e(TAG, "人脸添加失败 FileNotFoundException " + e.getMessage());
            e.printStackTrace();
            bool = false;
        } catch (IOException e) {
            Log.e(TAG, "人脸添加失败 IOException ");
            DLLog.e(TAG, "人脸添加失败 IOException " + e.getMessage());
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }

    /**
     * 注册人脸
     *
     * @param context 上下文对象
     * @param nv21    NV21数据
     * @param width   NV21宽度
     * @param height  NV21高度
     * @param name    保存的名字，可为空
     * @return 是否注册成功
     */
//    public boolean register(Context context, byte[] nv21, int width, int height, String name) {
//        synchronized (this) {
//            if (faceEngine == null || context == null || nv21 == null || width % 4 != 0 || nv21.length != width *
// height * 3 / 2) {
//                return false;
//            }
//
//            if (ROOT_PATH == null) {
//                ROOT_PATH = context.getFilesDir().getAbsolutePath();
//            }
//            boolean dirExists = true;
//            //特征存储的文件夹
//            File featureDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR);
//            if (!featureDir.exists()) {
//                dirExists = featureDir.mkdirs();
//            }
//            if (!dirExists) {
//                return false;
//            }
//            //图片存储的文件夹
//            File imgDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR);
//            if (!imgDir.exists()) {
//                dirExists = imgDir.mkdirs();
//            }
//            if (!dirExists) {
//                return false;
//            }
//            //1.人脸检测
//            List<FaceInfo> faceInfoList = new ArrayList<>();
//            int code = faceEngine.detectFaces(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
//            if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
//                FaceFeature faceFeature = new FaceFeature();
//
//                //2.特征提取
//                code = faceEngine.extractFaceFeature(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList.get
// (0), faceFeature);
//                String userName = name == null ? String.valueOf(System.currentTimeMillis()) : name;
//                try {
//                    //3.保存注册结果（注册图、特征数据）
//                    if (code == ErrorInfo.MOK) {
//                        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
//                        //为了美观，扩大rect截取注册图
//                        Rect cropRect = getBestRect(width, height, faceInfoList.get(0).getRect());
//                        if (cropRect == null) {
//                            return false;
//                        }
//                        File file = new File(imgDir + File.separator + userName + IMG_SUFFIX);
//                        FileOutputStream fosImage = new FileOutputStream(file);
//                        yuvImage.compressToJpeg(cropRect, 100, fosImage);
//                        fosImage.close();
//                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//                        //判断人脸旋转角度，若不为0度则旋转注册图
//                        boolean needAdjust = false;
//                        if (bitmap != null) {
//                            switch (faceInfoList.get(0).getOrient()) {
//                                case FaceEngine.ASF_OC_0:
//                                    break;
//                                case FaceEngine.ASF_OC_90:
//                                    bitmap = ImageUtil.getRotateBitmap(bitmap, 90);
//                                    needAdjust = true;
//                                    break;
//                                case FaceEngine.ASF_OC_180:
//                                    bitmap = ImageUtil.getRotateBitmap(bitmap, 180);
//                                    needAdjust = true;
//                                    break;
//                                case FaceEngine.ASF_OC_270:
//                                    bitmap = ImageUtil.getRotateBitmap(bitmap, 270);
//                                    needAdjust = true;
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                        if (needAdjust) {
//                            fosImage = new FileOutputStream(file);
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosImage);
//                            fosImage.close();
//                        }
//
//                        FileOutputStream fosFeature = new FileOutputStream(featureDir + File.separator + userName);
//                        fosFeature.write(faceFeature.getFeatureData());
//                        fosFeature.close();
//
//                        //内存中的数据同步
//                        if (faceRegisterInfoList == null) {
//                            faceRegisterInfoList = new ArrayList<>();
//                        }
//                        // TODO: 2019/3/15 添加
//                        faceRegisterInfoList.add(new FaceRegisterInfo(faceFeature.getFeatureData(), userName));
//                        return true;
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return false;
//        }
//
//    }

    /**
     * 在特征库中搜索
     *
     * @param faceFeature 传入特征数据
     * @return 比对结果
     */
//    public CompareResult getTopOfFaceLib(FaceFeature faceFeature) {
//        if (faceEngine == null || isProcessing || faceFeature == null || faceRegisterInfoList == null ||
// faceRegisterInfoList.size() == 0) {
//            return null;
//        }
//        FaceFeature tempFaceFeature = new FaceFeature();
//        FaceSimilar faceSimilar = new FaceSimilar();
//        float maxSimilar = 0;
//        int maxSimilarIndex = -1;
//        isProcessing = true;
//        for (int i = 0; i < faceRegisterInfoList.size(); i++) {
//            tempFaceFeature.setFeatureData(faceRegisterInfoList.get(i).getFeatureData());
//            faceEngine.compareFaceFeature(faceFeature, tempFaceFeature, faceSimilar);
//            if (faceSimilar.getScore() > maxSimilar) {
//                maxSimilar = faceSimilar.getScore();
//                maxSimilarIndex = i;
//            }
//        }
//        isProcessing = false;
//        if (maxSimilarIndex != -1) {
//            return new CompareResult(faceRegisterInfoList.get(maxSimilarIndex).getName(), maxSimilar);
//        }
//        return null;
//    }
}
