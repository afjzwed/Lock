package com.cxwl.menjin.lock.config;


/**
 * Created by simon on 2016/7/23.
 */
public class DeviceConfig {

//    volatile 防止并发,适用于对变量的写操作不依赖于当前值,对变量的读取操作不依赖于非volatile变量,适用于读多写少的场景
    /********residential*****/

    public static final String LOCAL_FILE_PATH = "adv";//广告储存位置
    public static final String LOCAL_VOICE_PATH = "myvoice";//开门用音频及图片储存位置
    public static final String LOCAL_ADP_PATH = "adp";//广告图片储存位置
    public static final String LOCAL_FACE_PATH = "myface";//人脸图片(bin文件)储存位置(下载)
    public static final String LOCAL_APK_PATH = "myapk";//apk文件储存位置
    public static final String LOCAL_FACEINFO_PATH = "myfaceinfo";//人脸数据库储存位置
    public static final String LOCAL_FACE_PATH_TEMP = "tempface";//人脸图片(bin文件)临时储存位置(上传)
    public static final String LOCAL_IMG_PATH = "img_local";//所有照片
    public static final String NEW_LOCAL_FACE_PATH = "newmyface";//人脸图片(bin文件)储存位置(下载)
    public static final String NEW_LOCAL_FACEINFO_PATH = "newfaceinfo";//人脸数据库储存位置

    public static final String LOCAL_VOICE_NAME = "menjinkaimen";//开门用音频文件名 mp3
    public static final String LOCAL_IMG_NAME = "bg_dialog";//开门用照片文件名 png
    public static boolean isLocalVoiceHint = false;//是否使用本地音频提示的标志 默认不使用
    public static boolean isLocalPicHint = false;//是否使用本地图片提示的标志 默认不使用

    public static boolean isLocalHint = false;//是否使用本地音频提示的标志 默认不使用

    public static boolean isReconnect = false;//WebSocket是否重连的标志 默认不重连

    public static String DEVICE_TYPE = "B"; //C：社区大门门禁 B:楼栋单元门禁
    public static String imgName = ""; //图片名字的年月数字，门禁编号

    public static String RFID_PORT = "/dev/ttyS1";//卡阅读器使用

    public static int OPENDOOR_TIME = 1000 * 5;//开门持续时间
    public static int OPENDOOR_STATE = 0;//开门状态 0为关闭门锁 1为打开门锁 默认关闭
    public static int CANCEL_CALL_WAIT_TIME = 1000 * 30;//自动取消呼叫等待时间
    public static int PASSWORD_WAIT_TIME = 1000 * 20;//密码验证线程等待时间

    public static int VOLUME_STREAM_MUSIC = 5;//音乐音量
    public static int VOLUME_STREAM_VOICE_CALL = 80;//通话音量
    public static int VOLUME_STREAM_RING = 5;//铃声音量
    public static int VOLUME_STREAM_SYSTEM = 5;//系统音量
    public static int VOLUME_ALL = 5;//总音量

    public static int LIGHT_ALL = 90;//屏幕亮度

    public static int MOBILE_NO_LENGTH = 11;//手机号长度
    public static int UNIT_NO_LENGTH = 4;//房屋号长度
    public static int BLOCK_LENGTH = 8;//楼栋房屋号长度
    public static int RTC_RELOAD_COUNT = 20;//天翼RTC自动重连次数,重连超过该次数后则停止重连,断开RTC连接

    // TODO: 2019/2/12 尝试用volatile关键字修饰,避免并发错误?
    public volatile static int PRINTSCREEN_STATE = 0;//各种方式(人脸/卡通过人脸线程截图)是否开始处理图片并上传日志的状态  0:未开始 1:人脸 2:卡成功 3:代表人脸线程未开

    public static final int DEVICE_KEYCODE_POUND = 66;//确认键
    public static final int DEVICE_KEYCODE_STAR = 67;//删除键
//    public static final int DEVICE_KEYCODE_POUND = 30;//确认键
//    public static final int DEVICE_KEYCODE_STAR = 32;//删除键

    public static final String Lockaxial_Monitor_PackageName = "com.cxwl.lock.monitor";
    public static final String Lockaxial_Monitor_SERVICE = "com.cxwl.lock.monitor.MonitorService";
    public static final String Lockaxial_PackageName = "com.cxwl.menjin.lock";

    public static final String SD_PATH = "CXWL/Monitor";

    /*******************************/

}
