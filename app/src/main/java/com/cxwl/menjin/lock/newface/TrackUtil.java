package com.cxwl.menjin.lock.newface;

import com.arcsoft.face.FaceInfo;

import java.util.List;

/**
 * Created by William on 2020/3/19.
 */

public class TrackUtil {
    public static boolean isSameFace(FaceInfo faceInfo1, FaceInfo faceInfo2) {
        return faceInfo1.getFaceId() == faceInfo2.getFaceId();
    }

    public static void keepMaxFace(List<FaceInfo> ftFaceList) {
        if (ftFaceList == null || ftFaceList.size() <= 1) {
            return;
        }
        FaceInfo maxFaceInfo = ftFaceList.get(0);
        for (FaceInfo faceInfo : ftFaceList) {
            if (faceInfo.getRect().width() > maxFaceInfo.getRect().width()) {
                maxFaceInfo = faceInfo;
            }
        }
        ftFaceList.clear();
        ftFaceList.add(maxFaceInfo);
    }
}
