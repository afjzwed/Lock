package com.cxwl.menjin.lock.http;

/**
 * Created by William on 2018/5/10.
 */

public interface API {
    //        String HTTP_HOST = "http://192.168.8.132:80";
//    String HTTP_HOST = "http://119.23.139.180:8080/wygl_door";
//    String HTTP_HOST = "http://192.168.8.34:8080/api/xdoor";//测试
    String HTTP_HOST = "http://47.106.143.169:8082/api/xdoor";//线上

//    String DEVICE_LOGIN = HTTP_HOST + "/xdoor/device/deviceLogin";//登录

//    String CONNECT_REPORT = HTTP_HOST + "/xdoor/device/connectReport";//心跳

//    String CALLALL_MEMBERS = HTTP_HOST + "/xdoor/device/callAllMembers";//获取成员

//    String OPENDOOR_BYTEMPKEY = HTTP_HOST + "/xdoor/device/openDoorByTempKey";//密码验证

//    String LOG = HTTP_HOST + "/xdoor/device/createAccessLog";//日志提交 开门方式:1卡2手机3人脸4邀请码5离线密码6临时密码'

//    String CALLALL_CARDS = HTTP_HOST + "/xdoor/device/callAllCards";//获取门禁卡信息

//    String CALLALL_NOTICES = HTTP_HOST + "/xdoor/device/callAllNotices";//获取通告信息

//    String SYNC_CALLBACK = HTTP_HOST + "/xdoor/device/syncCallBack";//同步完成通知

//    String CALLALL_FACES = HTTP_HOST + "/xdoor/device/callAllFaces";//获取人脸信息

//    String CALLALL_ADS = HTTP_HOST + "/xdoor/device/callAllAds";//获取广告信息

//    String VERSION_ADDRESS = HTTP_HOST + "/xdoor/device/version";//获取版本更新路径

//    String ADV_TONGJI = HTTP_HOST + "/xdoor/device/tongji";//广告播放统计

//    String QINIU_IMG = HTTP_HOST + "/qiniu/getQiniuToken";//获取七牛图片路径

//    String NEW_TONGJI = HTTP_HOST + "/xdoor/device/tongjiPlSave";//新统计接口

    String PIC = "http://img0.hnchxwl.com/";//七牛图片访问地址

    String DEVICE_LOGIN = HTTP_HOST + "/xsq/web/xdoor/jiqi/deviceLogin";//登录 已完成

    String CONNECT_REPORT = HTTP_HOST + "/xsq/web/xdoor/jiqi/connectReport";//心跳 已完成

    String CALLALL_CARDS = HTTP_HOST + "/xsq/web/xdoor/jiqi/callAllCards";//获取门禁卡信息 已完成

    String SYNC_CALLBACK = HTTP_HOST + "/xsq/web/xdoor/jiqi/syncCallBack";//同步完成通知 已完成

    String CALLALL_FACES = HTTP_HOST + "/xsq/web/xdoor/jiqi/callAllFaces";//获取人脸信息 已完成

    String CALLALL_ADS = HTTP_HOST + "/xsq/web/xdoor/jiqi/callAllAds";//获取广告信息 已完成

    String CALLALL_NOTICES = HTTP_HOST + "/xsq/web/xdoor/jiqi/callAllNotices";//获取通告信息 已完成

    String ADV_TONGJI = HTTP_HOST + "/xsq/web/xdoor/jiqi/tongji";//广告播放统计 已完成

    String OPENDOOR_BYTEMPKEY = HTTP_HOST + "/xsq/web/xdoor/jiqi/openDoorByTempKey";//密码验证 已完成

    String LOG = HTTP_HOST + "/xsq/web/xdoor/jiqi/createAccessLog";//日志提交 开门方式:1卡2手机3人脸4邀请码5离线密码6临时密码' 已完成

    String CALLALL_MEMBERS = HTTP_HOST + "/xsq/web/xdoor/jiqi/callAllMembers";//获取成员 已完成

    String QINIU_IMG = "http://119.23.139.180:8080/wygl_door" + "/qiniu/getQiniuToken";//获取七牛图片路径 已完成

    String NEW_TONGJI = HTTP_HOST + "/xsq/web/xdoor/jiqi/tongji";//新统计接口 已完成

    String VERSION_ADDRESS = HTTP_HOST + "/xsq/web/xdoor/jiqi/version";//获取版本更新路径 已完成

    String CALLALL_HINT = HTTP_HOST + "/xsq/web/xdoor/jiqi/callAllTishi";//获取提示信息 已完成

}
