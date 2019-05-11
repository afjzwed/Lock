package com.cxwl.menjin.lock.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.arcsoft.facetracking.AFT_FSDKEngine;
import com.arcsoft.facetracking.AFT_FSDKError;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKVersion;
import com.bumptech.glide.Glide;
import com.cxwl.menjin.lock.MainApplication;
import com.cxwl.menjin.lock.R;
import com.cxwl.menjin.lock.callback.AdverErrorCallBack;
import com.cxwl.menjin.lock.callback.AdverTongJiCallBack;
import com.cxwl.menjin.lock.config.Constant;
import com.cxwl.menjin.lock.config.DeviceConfig;
import com.cxwl.menjin.lock.db.AdTongJiBean;
import com.cxwl.menjin.lock.db.ImgFile;
import com.cxwl.menjin.lock.entity.FaceRegist;
import com.cxwl.menjin.lock.entity.GuangGaoBean;
import com.cxwl.menjin.lock.entity.NewDoorBean;
import com.cxwl.menjin.lock.entity.NoticeBean;
import com.cxwl.menjin.lock.entity.ResponseBean;
import com.cxwl.menjin.lock.face.ArcsoftManager;
import com.cxwl.menjin.lock.face.PhotographActivity2;
import com.cxwl.menjin.lock.http.API;
import com.cxwl.menjin.lock.interfac.TakePictureCallback;
import com.cxwl.menjin.lock.service.MainService;
import com.cxwl.menjin.lock.utils.AdvertiseHandler;
import com.cxwl.menjin.lock.utils.BitmapUtils;
import com.cxwl.menjin.lock.utils.CardRecord;
import com.cxwl.menjin.lock.utils.DLLog;
import com.cxwl.menjin.lock.utils.DbUtils;
import com.cxwl.menjin.lock.utils.DialogUtil;
import com.cxwl.menjin.lock.utils.HttpApi;
import com.cxwl.menjin.lock.utils.HttpUtils;
import com.cxwl.menjin.lock.utils.InstallUtil;
import com.cxwl.menjin.lock.utils.Intenet;
import com.cxwl.menjin.lock.utils.JsonUtil;
import com.cxwl.menjin.lock.utils.MacUtils;
import com.cxwl.menjin.lock.utils.NetWorkUtils;
import com.cxwl.menjin.lock.utils.SPUtil;
import com.cxwl.menjin.lock.utils.ShellUtils;
import com.cxwl.menjin.lock.utils.SoundPoolUtil;
import com.cxwl.menjin.lock.utils.StringUtils;
import com.cxwl.menjin.lock.view.AutoScrollView;
import com.google.gson.reflect.TypeToken;
import com.guo.android_extend.java.AbsLoop;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.youth.banner.Banner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android_serialport_api.ComBean;
import android_serialport_api.SerialHelper;
import jni.util.Utils;
import okhttp3.Call;

import static com.cxwl.menjin.lock.config.Constant.CALLING_MODE;
import static com.cxwl.menjin.lock.config.Constant.CALL_MODE;
import static com.cxwl.menjin.lock.config.Constant.ERROR_MODE;
import static com.cxwl.menjin.lock.config.Constant.MAIN_ACTIVITY_INIT;
import static com.cxwl.menjin.lock.config.Constant.MSG_ADVERTISE_IMAGE;
import static com.cxwl.menjin.lock.config.Constant.MSG_ADVERTISE_REFRESH;
import static com.cxwl.menjin.lock.config.Constant.MSG_ADVERTISE_REFRESH_PIC;
import static com.cxwl.menjin.lock.config.Constant.MSG_CALLMEMBER_ERROR;
import static com.cxwl.menjin.lock.config.Constant.MSG_CALLMEMBER_NO_ONLINE;
import static com.cxwl.menjin.lock.config.Constant.MSG_CALLMEMBER_SERVER_ERROR;
import static com.cxwl.menjin.lock.config.Constant.MSG_CALLMEMBER_TIMEOUT;
import static com.cxwl.menjin.lock.config.Constant.MSG_CANCEL_CALL;
import static com.cxwl.menjin.lock.config.Constant.MSG_CANCEL_ONVIDEO;
import static com.cxwl.menjin.lock.config.Constant.MSG_CARD_INCOME;
import static com.cxwl.menjin.lock.config.Constant.MSG_CARD_KEY_INCOM;
import static com.cxwl.menjin.lock.config.Constant.MSG_CARD_OPENLOCK;
import static com.cxwl.menjin.lock.config.Constant.MSG_CHECK_PASSWORD;
import static com.cxwl.menjin.lock.config.Constant.MSG_DELETE_FACE;
import static com.cxwl.menjin.lock.config.Constant.MSG_DISCONNECT_VIEDO;
import static com.cxwl.menjin.lock.config.Constant.MSG_FACE_DETECT_CHECK;
import static com.cxwl.menjin.lock.config.Constant.MSG_FACE_DETECT_CONTRAST;
import static com.cxwl.menjin.lock.config.Constant.MSG_FACE_DETECT_INPUT;
import static com.cxwl.menjin.lock.config.Constant.MSG_FACE_DETECT_PAUSE;
import static com.cxwl.menjin.lock.config.Constant.MSG_FACE_INFO;
import static com.cxwl.menjin.lock.config.Constant.MSG_FACE_INFO_FINISH;
import static com.cxwl.menjin.lock.config.Constant.MSG_FACE_OPENLOCK;
import static com.cxwl.menjin.lock.config.Constant.MSG_GET_NOTICE;
import static com.cxwl.menjin.lock.config.Constant.MSG_INPUT_CARDINFO;
import static com.cxwl.menjin.lock.config.Constant.MSG_INVALID_CARD;
import static com.cxwl.menjin.lock.config.Constant.MSG_LIXIAN_PASSWORD_CHECK_AFTER;
import static com.cxwl.menjin.lock.config.Constant.MSG_LOADLOCAL_DATA;
import static com.cxwl.menjin.lock.config.Constant.MSG_LOCK_OPENED;
import static com.cxwl.menjin.lock.config.Constant.MSG_LOGIN_AFTER;
import static com.cxwl.menjin.lock.config.Constant.MSG_LOGIN_FAILED;
import static com.cxwl.menjin.lock.config.Constant.MSG_PASSWORD_CHECK;
import static com.cxwl.menjin.lock.config.Constant.MSG_RESTART_VIDEO;
import static com.cxwl.menjin.lock.config.Constant.MSG_RTC_DISCONNECT;
import static com.cxwl.menjin.lock.config.Constant.MSG_RTC_NEWCALL;
import static com.cxwl.menjin.lock.config.Constant.MSG_RTC_ONVIDEO;
import static com.cxwl.menjin.lock.config.Constant.MSG_RTC_REGISTER;
import static com.cxwl.menjin.lock.config.Constant.MSG_START_DIAL;
import static com.cxwl.menjin.lock.config.Constant.MSG_START_DIAL_PICTURE;
import static com.cxwl.menjin.lock.config.Constant.MSG_TONGJI_VEDIO;
import static com.cxwl.menjin.lock.config.Constant.MSG_UPDATE_NETWORKSTATE;
import static com.cxwl.menjin.lock.config.Constant.MSG_UPLOAD_LIXIAN_IMG;
import static com.cxwl.menjin.lock.config.Constant.MSG_UPLOAD_LOG;
import static com.cxwl.menjin.lock.config.Constant.MSG_YIJIANKAIMEN_TAKEPIC;
import static com.cxwl.menjin.lock.config.Constant.MSG_YIJIANKAIMEN_TAKEPIC1;
import static com.cxwl.menjin.lock.config.Constant.ONVIDEO_MODE;
import static com.cxwl.menjin.lock.config.Constant.PASSWORD_CHECKING_MODE;
import static com.cxwl.menjin.lock.config.Constant.PASSWORD_MODE;
import static com.cxwl.menjin.lock.config.Constant.REGISTER_ACTIVITY_DIAL;
import static com.cxwl.menjin.lock.config.Constant.START_FACE_CHECK;
import static com.cxwl.menjin.lock.config.Constant.START_FACE_CHECK1;
import static com.cxwl.menjin.lock.config.Constant.START_FACE_CHECK2;
import static com.cxwl.menjin.lock.config.Constant.START_FACE_CHECK3;
import static com.cxwl.menjin.lock.config.Constant.arc_appid;
import static com.cxwl.menjin.lock.config.Constant.fr_key;
import static com.cxwl.menjin.lock.config.Constant.ft_key;
import static com.cxwl.menjin.lock.config.Constant.identification;
import static com.cxwl.menjin.lock.config.DeviceConfig.DEVICE_KEYCODE_POUND;
import static com.cxwl.menjin.lock.config.DeviceConfig.DEVICE_KEYCODE_STAR;
import static com.cxwl.menjin.lock.config.DeviceConfig.LOCAL_IMG_PATH;
import static com.cxwl.menjin.lock.service.MainService.RTC_AVAILABLE;
import static com.cxwl.menjin.lock.utils.NetWorkUtils.NETWOKR_TYPE_ETHERNET;
import static com.cxwl.menjin.lock.utils.NetWorkUtils.NETWOKR_TYPE_MOBILE;
import static com.cxwl.menjin.lock.utils.NetWorkUtils.NETWORK_TYPE_NONE;
import static com.cxwl.menjin.lock.utils.NetWorkUtils.NETWORK_TYPE_WIFI;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CameraSurfaceView
        .OnCameraListener, TakePictureCallback, SerialHelper.onDataReceived {

    private static String TAG = "MainActivity";

    public static int currentStatus = CALL_MODE;//当前状态
    public static final int INPUT_SYSTEMSET_REQUESTCODE = 0X03;

    private TextView version_text;//版本名显示
    private View container;//根View
    private LinearLayout videoLayout;
    private RelativeLayout rl_nfc, rl;//录卡布局和网络检测提示布局
    private ImageView iv_setting, imageView, wifi_image;
    private TextView headPaneTextView, tv_message, tv_battery, showMacText;
    private EditText et_blackno, et_unitno, tv_input_text;
    private TextView tv_gonggao_title;
    private TextView tv_gonggao;
    private AutoScrollView as;

    private Handler handler;
    private Messenger mainMessage;
    private Messenger serviceMessage;//Service端的Messenger
    private String mac;//Mac地址
    private boolean isFlag = true;//录卡时楼栋编号焦点监听的标识
//    private NfcReader nfcReader;//用于nfc卡扫描

    private SurfaceView localView = null;//rtc本地摄像头view
    private SurfaceView remoteView = null;//rtc远端视频view
    private String blockNo = "";//输入的房号
    private int blockId;//输入的房号
    private HashMap<String, String> uuidMaps = new HashMap<String, String>();//储存uuid
    private String lastImageUuid = ""; //与拍照图片相对应

    private Camera camera = null;
    private SurfaceHolder autoCameraHolder = null;
    private SurfaceView autoCameraSurfaceView = null;
    private AdvertiseHandler advertiseHandler = null;//广告播放类
//    public appLibsService hwservice;//hwservice为安卓工控appLibs的服务

    private Thread videoThread = null;//视频更新线程
    private boolean isVideoThreadStart = false;//视频更新线程是否开启的标志
    private boolean isNfcFlag = false;//串口库是否打开的标识（默认失败）
    //    private DoorLock doorLock;//用于nfc卡扫描
    private SerialHelper control;// 串口
    private Dialog weituoDialog = null;

    private String cardId;//卡ID
    private boolean nfcFlag = false;//录卡页面是否显示(即是否录卡)的标识,默认false
    //    private Receive receive; //本地广播
    private int netWorkFlag = -1;//当前网络是否可用标识 有网为1 无网为0
    private Timer netTimer = new Timer();//检测网络用定时器
    private Banner banner;
    private ImageView imgBanner;
    private WifiInfo wifiInfo = null;//获得的Wifi信息
    private int level;//信号强度值
    private WifiManager wifiManager = null;//Wifi管理器

    private UploadManager uploadManager;//七牛上传

    private Thread clockRefreshThread = null;
    private Timer facetimer = new Timer();
    private Timer rtcTimer = new Timer();

    private Thread passwordTimeoutThread = null;//访客密码线程
    private String guestPassword = "";//访客输入的密码值

    private int mWidth, mHeight;//屏幕宽高
    private CameraSurfaceView mSurfaceView;//用于人脸识别（单独使用则渲染直接走系统流程，配合CameraGLSurfaceView
    // 这个渲染显示的控件来用的的话，。CameraGLSurfaceView 这个类里使用了GLRender
    // 这个GL渲染类，把Camera的数据放进去就可以等比例显示，当然也可以设置不等比例的显示，支持旋转，支持输出渲染帧率）
    private CameraGLSurfaceView mGLSurfaceView;//用于人脸识别
    private Camera mCamera;//虹软要使用的摄像头对象
    private AFT_FSDKVersion version = new AFT_FSDKVersion();//这个类用来保存版本信息
    private AFT_FSDKEngine engine = new AFT_FSDKEngine();//这个类具体实现了人脸跟踪的功能
    private List<AFT_FSDKFace> result = new ArrayList<>();//摄像头检测到的人脸信息集合
    private byte[] mImageNV21 = null;//人脸图像数据
    private byte[] picData = null;//刷卡时的图像数据
    private AFT_FSDKFace mAFT_FSDKFace = null;//这个类用来保存检测到的人脸信息
    private Handler faceHandler;//人脸识别handler

    private FRAbsLoop mFRAbsLoop = null;//人脸对比线程
    private Thread picThread = null;//图片更新线程
    private boolean isPicThreadStart = false;//图片更新线程是否开启的标志
    protected CardRecord cardRecord = new CardRecord();

    private Thread noticeThread = null;//通告更新线程
    private boolean isTongGaoThreadStart = false;//通告更新线程是否开启的标志
    private ArrayList<NoticeBean> noticeBeanList = new ArrayList<>();//通告集合
    private NoticeBean currentNoticeBean = null;//当前显示通告
    private NoticeBean defaultNotice = null;//默认通告
    private int tongGaoIndex = 0;//通告更新计数
    private boolean isTongGaoFrist = true;//通告是否是第一次滚动

    private boolean mCamerarelease = true; //判断照相机是否释放
    private Handler cameraHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                if (mCamerarelease) {
                    cameraHandler.removeMessages(0x01);
                    buildVideo();
                } else {
                    cameraHandler.sendEmptyMessageDelayed(0x01, 200);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        //全屏设置，隐藏窗口所有装饰
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);//清除FLAG
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        window.setAttributes(params);//隐藏虚拟按键

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//禁止软键盘弹出

        {
            ActionBar ab = getActionBar();
            if (ab != null) {
                ab.setDisplayHomeAsUpEnabled(true);//左上角显示应用程序图标
            }
        }

        setContentView(R.layout.activity_main);

        DLLog.e(TAG, "应用开启");

        initView();//初始化View
        initQiniu();//初始化七牛
        initScreen();
        initHandle();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.v("人脸识别", "initFaceDetect-->" + 111);
//                boolean b = ArcsoftManager.getInstance().mFaceDB.loadFaces();
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (ArcsoftManager.getInstance().mFaceDB.mRegister.isEmpty()) {
//                            Log.v("人脸识别", "initFaceDetect-->" + 333);
////                            Utils.DisplayToast(MainActivity.this, "没有注册人脸，请先注册");
//                            return;
//                        }
//                        identification = true;
//                        Log.v("人脸识别", "initFaceDetect-->" + 222);
//                        Utils.DisplayToast(MainActivity.this, "人脸数据加载完成" + ArcsoftManager.getInstance().mFaceDB
//                                .mRegister.size());
//                    }
//                });
//            }
//        }).start();

        initAexNfcReader();//初始化nfc本地广播
        initMainService();
        initVoiceVolume();//初始化音量设置

//        initLightVolume();//初始化屏幕亮度设置

        initAutoCamera();
        startClockRefresh();//时间更新线程,在有心跳包后用心跳包代替
        // TODO: 2018/12/29 测试 隔段时间重开一次摄像头试试
//        startFaceRefresh();//隔段时间重开一次摄像头
        initNet();
        initAdvertiseHandler();

        // TODO: 2018/9/28 两台设备不一样
        initLightTimer();//延时开关灯定时器初始化

        if ("C".equals(DeviceConfig.DEVICE_TYPE)) {//判断是否社区大门
            setDialStatus("请输入楼栋编号");
        }

        //初始化人脸相关与身份证识别
        initFaceDetectAndIDCard();

        // TODO: 2018/12/29 测试
//        initFaceDetectAndIDCard1();

        isTongGaoThreadStart = false;//每次初始化都重启一次通告更新线程
        isPicThreadStart = false;//每次初始化都重启一次图片更新线程
        isVideoThreadStart = false;//每次初始化都重启一次视频更新线程

    }

    /********************************************初始化一些基本东西start******************************************/

    /**************************通告轮播***************/
    /**
     * 开启通告更新线程
     */
    private void startTonggaoThread() {
        defaultNotice = new NoticeBean();
        defaultNotice.setBiaoti("暂无通知");
        defaultNotice.setNeirong("暂无通知");
        defaultNotice.setShixiao_shijian("2050-06-02 10:17:00.0");

        if (null != noticeThread) {
            noticeThread.interrupt();
            noticeThread = null;
        }
        Log.e(TAG, "通告线程开始" + isTongGaoThreadStart);
        noticeThread = new Thread() {
            @Override
            public void run() {
                try {
                    setTongGaoInfo();
                    while (!isInterrupted()) {//检测线程是否已经中断
                        sleep(1000 * 60);//间隔时间
                        isTongGaoFrist = false;
                        setTongGaoInfo();
                    }
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 startTonggaoThread catch-> " + e.toString());
                    Log.e(TAG, "错误 startTonggaoThread catch-> " + e.toString());
                    e.printStackTrace();
                }
            }
        };
        noticeThread.start();
    }

    /**
     * 开启界面时间(本地)更新线程 之后放到MainService中
     */
    private void startClockRefresh() {
        clockRefreshThread = new Thread() {
            public void run() {
                try {
                    setNewTime();
                    while (true) {
                        sleep(1000 * 60); //等待指定的一个时间(一分钟显示一次)
                        if (!isInterrupted()) { //检查线程没有被停止
                            setNewTime();
                        }
                    }
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 startClockRefresh catch-> " + e.toString());
                    Log.e(TAG, "错误 startClockRefresh catch-> " + e.toString());
                    e.printStackTrace();
                }
                clockRefreshThread = null;// TODO: 2019/1/3 这里为什么要置为null?
            }
        };
        clockRefreshThread.start();
    }

    /**
     * 设置界面时间(本地)
     */
    private void setNewTime() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("E");//星期
                String dayStr = dateFormat.format(now);
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateStr = dateFormat.format(now);
                dateFormat = new SimpleDateFormat("HH:mm");
                String timeStr = dateFormat.format(now);

                setTextView(R.id.tv_day, dayStr);
                setTextView(R.id.tv_date, dateStr);
                setTextView(R.id.tv_time, timeStr);
            }
        });
    }
    /**************************通告轮播***************/
    /**************************视频轮播***************/
    private List<GuangGaoBean> videoList;
    private List<GuangGaoBean> isPlayingList = new ArrayList<>();

    private void startVedioThread() {
        if (null != videoThread) {
            videoThread.interrupt();
            videoThread = null;
        }
        videoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setVideoInfo();
                    while (!Thread.interrupted()) {
                        sleep(30000);
                        setVideoInfo();
                    }
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 startVedioThread catch-> " + e.toString());
                    Log.e(TAG, "错误 startVedioThread catch-> " + e.toString());
                    e.printStackTrace();
                }
            }
        });
        videoThread.start();
    }

    private void setVideoInfo() {
        if (isVideoStart()) {
            isPlayingList.clear();
            //有开始播放的视频
            for (int i = 0; i < videoList.size(); i++) {
                if (!TextUtils.isEmpty(videoList.get(i).getShixiao_shijian()) && !TextUtils.isEmpty(videoList.get(i)
                        .getKaishi_shijian())) {
                    if (Long.parseLong(StringUtils.transferDateToLong(videoList.get(i).getShixiao_shijian())) > System
                            .currentTimeMillis()) {//过期时间大于当前时间
                        Log.e(TAG, "设置视频 有数据");
                        if (Long.parseLong(StringUtils.transferDateToLong(videoList.get(i).getKaishi_shijian())) <
                                System.currentTimeMillis()) {//开始时间小于当前时间，可以显示
                            isPlayingList.add(videoList.get(i));
                        }
                    }
                }
            }
            //已过过期时间全部失效
            if (isPlayingList.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        advertiseHandler.onDestroy();
                    }
                });
                return;
            }
            //之前未播放 有新数据播放
            if (isPlayingList.size() > 0 && advertiseHandler.getList().size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        advertiseHandler.initData(isPlayingList, mainMessage, (currentStatus == ONVIDEO_MODE),
                                adverErrorCallBack, adverTongJiCallBack);
                    }
                });
                return;
            }
            //正在播放 有新数据更新
            if (isSaveOrUpdate(advertiseHandler.getList(), isPlayingList)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        advertiseHandler.initData(isPlayingList, mainMessage, (currentStatus == ONVIDEO_MODE),
                                adverErrorCallBack, adverTongJiCallBack);
                    }
                });
            }
        } else {
            //没有开始播放的视频
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    advertiseHandler.onDestroy();
                }
            });

        }
    }

    public boolean isSaveOrUpdate(List<GuangGaoBean> oldList, List<GuangGaoBean> newList) {
        if (oldList.size() != newList.size()) {
            return true;
        } else {
            for (GuangGaoBean o : oldList) {
                if (!newList.contains(o)) {
                    return true;
                }
            }
            for (GuangGaoBean o : newList) {
                if (!oldList.contains(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isVideoStart() {
        Log.e(TAG, "开始视频广告判断");
        boolean b = false;
        if (null != videoList && videoList.size() > 0) {
            for (GuangGaoBean noticeBean : videoList) {
                if (Long.parseLong(StringUtils.transferDateToLong(noticeBean.getKaishi_shijian())) < System
                        .currentTimeMillis()) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }
    /**************************视频轮播***************/

    /**************************图片轮播***************/
    private void startPicTread() {
        // TODO: 2018/9/5 没测
        if (null != picThread) {
            picThread.interrupt();
            picThread = null;
        }
        picThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setPicInfo();
                    while (!Thread.interrupted()) {
                        sleep(10000);
                        setPicInfo();
                    }
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 startPicTread catch-> " + e.toString());
                    Log.e(TAG, "错误 startPicTread catch-> " + e.toString());
                    e.printStackTrace();
                }
            }
        });
        picThread.start();
    }

    private List<GuangGaoBean> picList;
    private GuangGaoBean currentGuangGaoBean;
    private int picIndex = 0;
    private long picStartTime;
    private long picEndTime;

    /**
     * 刷新广告图片
     *
     * @param obj
     */
    private List<AdTongJiBean> mTongJiBeanList;
    private AdTongJiBean mAdTongJiBean;

    private void setPicInfo() {
        if (isPicStart()) {
            if (null != picList && picList.size() > 0) {//通告列表有数据

                currentGuangGaoBean = picList.get(picIndex);
                picIndex++;
                if (picIndex == picList.size()) {//循环一遍以后，重置游标
                    picIndex = 0;
                }
            } else {//通告列表无数据
                // 没有图片
                // currentNoticeBean = defaultNotice;
                //设置图片信息
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(R.mipmap.bg_banner).into(imgBanner);
                    }
                });
                return;
            }
            Log.e(TAG, "设置广告图片 currentNoticeBean" + currentGuangGaoBean.toString());
            Log.e(TAG, "设置广告图片 过期时间 " + currentGuangGaoBean.getShixiao_shijian() + " 当前时间 " + StringUtils
                    .transferLongToDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
            Log.e(TAG, "设置广告图片 过期时间 " + StringUtils.transferDateToLong(currentGuangGaoBean.getShixiao_shijian()) + " "
                    + "当前时间" + " " + System.currentTimeMillis());
            if (Long.parseLong(StringUtils.transferDateToLong(currentGuangGaoBean.getShixiao_shijian())) > System
                    .currentTimeMillis()) {//过期时间大于当前时间
                Log.e(TAG, "设置广告图片 有数据");
                if (Long.parseLong(StringUtils.transferDateToLong(currentGuangGaoBean.getKaishi_shijian())) < System
                        .currentTimeMillis()) {//开始时间小于当前时间，可以显示
                    //设置图片信息
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(MainActivity.this).load(currentGuangGaoBean.getNeirong()).into(imgBanner);
                        }
                    });

                    //上传广告统计日志
                    picEndTime = System.currentTimeMillis();
                    mTongJiBeanList = new ArrayList<>();
                    mAdTongJiBean = new AdTongJiBean();
                    mAdTongJiBean.setStart_time(picStartTime + "");
                    mAdTongJiBean.setEnd_time(picEndTime + "");
                    mAdTongJiBean.setAd_id(currentGuangGaoBean.getId());
                    mAdTongJiBean.setMac(mac);
                    mTongJiBeanList.add(mAdTongJiBean);
                    DbUtils.getInstans().addAllTongji(mTongJiBeanList);
                    //   sendMainMessager(MSG_TONGJI_PIC, mTongJiBeanList);
                    picStartTime = picEndTime;


                } else {//开始时间大于当前时间，跳过，直接显示下一条
                    setPicInfo();
                }
            } else {
                picIndex--;
                if (picIndex == -1) {
                    Log.e(TAG, "广告图片清零");
                    picList.clear();
                    picList = null;
                } else {
                    Log.e(TAG, "移除一条广告图片");
                    picList.remove(picIndex);
                }
                setPicInfo();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //设置默认图片
                    Glide.with(MainActivity.this).load(R.mipmap.bg_banner).into(imgBanner);
                }
            });
        }
    }

    private boolean isPicStart() {
        // TODO: 2018/9/5 没测
        Log.e(TAG, "开始图片广告判断");
        boolean b = false;
        if (null != picList && picList.size() > 0) {
            for (GuangGaoBean noticeBean : picList) {
                if (Long.parseLong(StringUtils.transferDateToLong(noticeBean.getKaishi_shijian())) < System
                        .currentTimeMillis()) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }

    /**************************图片轮播***************/

    private TextView textViewGongGao;
    private SurfaceView videoView;
    private AdverErrorCallBack adverErrorCallBack;
    private AdverTongJiCallBack adverTongJiCallBack;

    protected void initAdvertiseHandler() {
        if (advertiseHandler == null) {
            advertiseHandler = new AdvertiseHandler();
        }
        videoView = (SurfaceView) findViewById(R.id.surface_view);
        imageView = (ImageView) findViewById(R.id.image_view);
        textViewGongGao = (TextView) findViewById(R.id.gonggao);
        Log.v("UpdateAdvertise", "------>start Update Advertise<------");
        advertiseHandler.init(textViewGongGao, videoView, imageView);
        adverErrorCallBack = new AdverErrorCallBack() {
            @Override
            public void ErrorAdver() {
                imageView.setVisibility(View.VISIBLE);
            }
        };
        adverTongJiCallBack = new AdverTongJiCallBack() {
            @Override
            public void sendTj(List<AdTongJiBean> list) {
                sendMainMessager(MSG_TONGJI_VEDIO, list);
            }
        };
    }

    /**
     * 初始化七牛
     */
    private void initQiniu() {
        long currentTimeMillis = System.currentTimeMillis();
//        String fileurl = Environment.getExternalStorageDirectory() + "/" + LOCAL_IMG_PATH + "/" + currentTimeMillis
// + ".jpg";
        String fileurl = Environment.getExternalStorageDirectory() + "/" + LOCAL_IMG_PATH;// TODO: 2019/1/3 这里只建父文件夹

        final File file = new File(fileurl);
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        String dirPath = fileurl;
        Recorder recorder = null;
        try {
            recorder = new FileRecorder(dirPath);
        } catch (Exception e) {
            DLLog.e(TAG, "错误 初始化七牛 catch-> " + e.toString());
            e.printStackTrace();
        }
//默认使用key的url_safe_base64编码字符串作为断点记录文件的文件名
//避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
        KeyGenerator keyGen = new KeyGenerator() {
            @Override
            public String gen(String key, File file) {
                // 不必使用url_safe_base64转换，uploadManager内部会处理
                // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
                return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
            }
        };
// 重用uploadManager。一般地，只需要创建一个uploadManager对象
//UploadManager uploadManager = new UploadManager(recorder);  // 1
//UploadManager uploadManager = new UploadManager(recorder, keyGen); // 2
        Configuration config = new Configuration.Builder().chunkSize(512 * 1024)        //
                // 分片上传时，每片的大小。 默认256K
                .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                .connectTimeout(10)           // 链接超时。默认10秒
                .useHttps(true)               // 是否使用https上传域名
                .responseTimeout(60)          // 服务器响应超时。默认60秒
                .recorder(recorder)           // recorder分片上传时，已上传片记录器。默认null
                .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(FixedZone.zone2)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        // 实例化一个上传的实例
        uploadManager = new UploadManager(config);
    }

    /**
     * 初始化音量设置
     */
    protected void initVoiceVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initVoiceVolume(audioManager, AudioManager.STREAM_MUSIC, DeviceConfig.VOLUME_STREAM_MUSIC);
        initVoiceVolume(audioManager, AudioManager.STREAM_RING, DeviceConfig.VOLUME_STREAM_RING);
        initVoiceVolume(audioManager, AudioManager.STREAM_SYSTEM, DeviceConfig.VOLUME_STREAM_SYSTEM);
        initVoiceVolume(audioManager, AudioManager.STREAM_VOICE_CALL, DeviceConfig.VOLUME_STREAM_VOICE_CALL);
    }

    /**
     * 设置具体音量
     *
     * @param audioManager
     * @param type
     * @param value
     */
    protected void initVoiceVolume(AudioManager audioManager, int type, int value) {
        int thisValue = audioManager.getStreamMaxVolume(type);//得到最大音量
        thisValue = thisValue * value / 30;//具体音量值
        audioManager.setStreamVolume(type, thisValue, AudioManager.FLAG_PLAY_SOUND);//调整音量时播放声音
    }

    /**
     * 初始化屏幕亮度设置
     */
    private void initLightVolume() {
        int systemBrightness = getSystemBrightness();
        Log.e(TAG, "屏幕亮度 systemBrightness " + systemBrightness);
    }

    /**
     * 获取当前设备Window系统亮度
     *
     * @return
     */
    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

    /**
     * 改变当前Window亮度
     *
     * @param brightness
     */
    public void changeAppBrightness(int brightness) {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    /**
     * 初始化卡阅读器
     */
    private void initAexNfcReader() {

        control = new SerialHelper();
        control.setDataReceived(this);
//        control.setPort("/dev/ttyS4");//门口机才能用ttyS4
        // TODO: 2018/9/28 两台设备不一样
        control.setPort("/dev/ttyS1");
        control.setBaudRate(9600);
        OpenComPort(control);
    }

    // ----------------------------------------------------关闭串口
    private void CloseComPort(SerialHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    // ----------------------------------------------------打开串口
    private void OpenComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
        } catch (SecurityException e) {
            Log.e(TAG, "打开串口失败:没有串口读/写权限!");
        } catch (IOException e) {
            Log.e(TAG, "打开串口失败:未知错误!");
        } catch (InvalidParameterException e) {
            Log.e(TAG, "打开串口失败:参数错误!");
        }
    }

    /**
     * 初始化视频通话布局(用于天翼rtc网络呼叫状态显示)
     */
    protected void initScreen() {
        headPaneTextView = (TextView) findViewById(R.id.header_pane);//可视对讲设备状态
        videoLayout = (LinearLayout) findViewById(R.id.ll_video);//用于添加视频通话的根布局
        setTextView(R.id.tv_community, MainService.communityName);
        setTextView(R.id.tv_lock, MainService.lockName);
    }

    /**
     * 初始化照相机的surefaceview
     */
    protected void initAutoCamera() {
        Log.v("MainActivity", "initAutoCamera-->");
        autoCameraSurfaceView = (SurfaceView) findViewById(R.id.autoCameraSurfaceview);
        autoCameraHolder = autoCameraSurfaceView.getHolder();
        autoCameraHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 初始化view
     */
    public void initView() {
        version_text = (TextView) findViewById(R.id.version_text);
        version_text.setText(getVersionName());
        container = findViewById(R.id.container);

        imageView = (ImageView) findViewById(R.id.iv_erweima);//二维码
        wifi_image = (ImageView) findViewById(R.id.wifi_image); //wifi图标控件初始化
        iv_setting = (ImageView) findViewById(R.id.iv_setting);//左上角弹出菜单按钮
        tv_message = (TextView) findViewById(R.id.tv_message);//录入卡提示信息
        tv_input_text = (EditText) findViewById(R.id.tv_input_text);//桌面会话状态的提示信息
        tv_battery = (TextView) findViewById(R.id.tv_battery);//显示蓝牙锁的电量
        rl = (RelativeLayout) findViewById(R.id.net_view_rl);//网络检测提示布局
        showMacText = (TextView) findViewById(R.id.show_mac);//mac地址
        videoLayout = (LinearLayout) findViewById(R.id.ll_video);//用于添加视频通话的根布局
        banner = (Banner) findViewById(R.id.banner);//
        imgBanner = (ImageView) findViewById(R.id.img_banner);//图片轮播替换banner
        tv_gonggao_title = (TextView) findViewById(R.id.gonggao_title);
        tv_gonggao = (TextView) findViewById(R.id.gonggao);
        as = (AutoScrollView) findViewById(R.id.as);
        //getBgBanners();// 网络获得轮播背景图片数据
        rl_nfc = (RelativeLayout) findViewById(R.id.rl_nfc);//删除脸信息布局(原录卡布局)
        et_unitno = (EditText) findViewById(R.id.et_unitno);//删除脸信息的手机号(录卡时房屋编号)

        rl.setOnClickListener(this);
        iv_setting.setOnClickListener(this);

        sendBroadcast(new Intent("com.android.action.hide_navigationbar"));//隱藏底部導航
        container.setSystemUiVisibility(13063);//设置状态栏显示与否,禁止頂部下拉


    }

    /**
     * 初始化handler
     */
    private void initHandle() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_LOGIN_AFTER:
                        //登陆成功后 设置信息 初始化rtc等
                        Log.i(TAG, "登陆成功后");
                        onLoginAfter(msg);
                        break;
                    case MSG_LOGIN_FAILED:
                        //登陆失败
                        showMacaddress((String) msg.obj);
                        break;
                    case MSG_RTC_ONVIDEO:
                        //接通视频通话
                        Log.i(TAG, "接通视频通话");
                        onRtcVideoOn();
                        break;
                    case MSG_RTC_DISCONNECT:
                        Log.i(TAG, "视频通话断开");
                        onRtcDisconnect(); //视频通话断开
                        handler.sendEmptyMessageDelayed(START_FACE_CHECK, 3000);//启动人脸识别
                        break;
                    case MSG_RTC_NEWCALL:
                        //收到新的来电
                        Log.i(TAG, "收到新的来电");
                        onRtcConnected();
                        break;
                    case MSG_CALLMEMBER_TIMEOUT:
                        Log.e(TAG, "呼叫超时");
                        onCallMemberError(msg.what);
                        break;
                    case MSG_CALLMEMBER_NO_ONLINE:
                        Log.e(TAG, "呼叫用户不在线");
                        onCallMemberError(msg.what);
                        break;
                    case MSG_CALLMEMBER_SERVER_ERROR:
                        Log.e(TAG, "服务器错误");
                        onCallMemberError(msg.what);
                        break;
                    case MSG_INPUT_CARDINFO:
                        Log.e(TAG, "重复录卡");
                        String obj = (String) msg.obj;
                        tv_message.setText(obj);
                        break;
                    case MSG_PASSWORD_CHECK:
                        Log.i(TAG, "服务器验证密码后的返回");
                        onPasswordCheck((ResponseBean) msg.obj);
                        break;
                    case MSG_LIXIAN_PASSWORD_CHECK_AFTER:
                        Log.i(TAG, "验证离线验证密码后");
                        onCheckLixianPasswordAfter(msg.obj == null ? null : (boolean) msg.obj);
                        break;
                    case MSG_FACE_INFO://人脸识别暂停
                        Log.e(TAG, "人脸92");
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 100);
                        break;
                    case MSG_FACE_INFO_FINISH://人脸录入完成，重新开始人脸识别
                        Log.e(TAG, "人脸91");
                        facetimer.schedule(new TimerTask() {
                            public void run() {
                                //execute the task
                                //删除单个人脸时处理速度可能过快，导致先人脸识别再人脸暂停，所以用定时器控制1秒后再人脸识别，保证在人脸暂停后
                                handler.sendEmptyMessage(START_FACE_CHECK);
                            }
                        }, 2000);
                        break;
                    case MSG_LOCK_OPENED://开锁
                        Log.i(TAG, "开锁");
                        onLockOpened((int) msg.obj);
                        if (weituoDialog == null) {
                            weituoDialog = DialogUtil.showBottomDialog(MainActivity.this);
                        }
                        if (!weituoDialog.isShowing()) {
                            weituoDialog.show();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    weituoDialog.dismiss();
                                }
                            }, 1000);
                        }
                        break;
                    case MSG_INVALID_CARD:
                        //无效房卡
                        Utils.DisplayToast(MainActivity.this, "无效卡号");
                        break;
                    case MSG_LOADLOCAL_DATA://离线模式
                        SPUtil.put(MainActivity.this, Constant.SP_XINTIAO_TIME, (long) (600 * 1000));//离线模式，改为10分钟一次
                        //加载本地数据显示到界面
                        setCommunityName(MainService.communityName);
                        setLockName(MainService.lockName);
                        break;
                    case MSG_GET_NOTICE: //获取通告成功
                        String value = (String) msg.obj;
                        noticeBeanList = (ArrayList<NoticeBean>) JsonUtil.parseJsonToList(value, new
                                TypeToken<List<NoticeBean>>() {
                                }.getType());
                        tongGaoIndex = 0;
                        if (!isTongGaoThreadStart) {//线程未开启
                            isTongGaoThreadStart = !isTongGaoThreadStart;
                            startTonggaoThread();//开启线程
                        }
                        break;
                    case MSG_ADVERTISE_REFRESH://刷新广告
                        // onAdvertiseRefresh(msg.obj);
                        videoList = (List<GuangGaoBean>) msg.obj;
                        if (!isVideoThreadStart) {//线程未开启
                            isVideoThreadStart = !isVideoThreadStart;
                            startVedioThread();//开启线程
                        }

                        break;
                    case MSG_ADVERTISE_REFRESH_PIC://刷新广告图片
                        Log.i(TAG, "刷新广告图片");
                        //  onAdvertiseRefreshPic((ArrayList<GuangGaoBean>) msg.obj);
                        picStartTime = System.currentTimeMillis();
                        picList = (ArrayList<GuangGaoBean>) msg.obj;
                        picIndex = 0;
                        if (!isPicThreadStart) {//线程未开启
                            isPicThreadStart = !isPicThreadStart;
                            startPicTread();//开启线程
                        }
                        break;
                    case MSG_ADVERTISE_IMAGE:
                        onAdvertiseImageChange(msg.obj);
                        break;
                    case MSG_DELETE_FACE:
                        boolean delete = (boolean) msg.obj;
                        rl_nfc.setVisibility(View.GONE);
                        nfcFlag = false;
                        isFlag = false;
                        if (delete) {//删除成功
                            Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {//没有此人脸信息或删除失败
                            Toast.makeText(MainActivity.this, "数据库中没有此人脸信息", Toast.LENGTH_SHORT).show();
                        }
                        Log.e(TAG, "人脸信息删除" + " delete " + delete);
                        break;
                    case MSG_YIJIANKAIMEN_TAKEPIC:
                        takePictureFromPhone((String) msg.obj);
                        break;
                    case MSG_UPLOAD_LIXIAN_IMG:
                        uploadImgs((List<ImgFile>) msg.obj);
                        break;
                    case MSG_RESTART_VIDEO:
                        if (currentStatus == CALL_MODE || currentStatus == PASSWORD_MODE) {
                            if (faceHandler != null) {
                                Constant.RESTART_PHONE = false;
                                faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 100);
                                onReStartVideo();
                            }
                        }
                        break;
                    case MSG_YIJIANKAIMEN_TAKEPIC1:
                        handler.sendEmptyMessage(START_FACE_CHECK);
                        break;
                    case MSG_UPLOAD_LOG://上传日志
                        if (currentStatus == CALL_MODE || currentStatus == PASSWORD_MODE) {
                            if (faceHandler != null) {
                                Constant.UPLOAD_LOG = false;
                                File file = DLLog.upLoadFile();
                                if (null != file) {
                                    uploadLogToQiNiu(file);
//                                    Log.e("上传日志七牛", "有日志" + file.getPath() + " " + file.getAbsolutePath());
                                } else {
                                    DLLog.d("上传日志七牛", "没有日志");
//                                    Log.e("上传日志七牛", "没有日志");
                                }
                            }
                        }
                        break;
                    case START_FACE_CHECK:
                        Log.e(TAG, "人脸识别前的准备 释放相机");
                        if (faceHandler != null && mCamerarelease && mGLSurfaceView != null && mSurfaceView != null) {
                            HttpApi.i("相机释放成功，开启人脸识别");
                            handler.removeMessages(START_FACE_CHECK);
                            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 1000);
                        } else {
                            HttpApi.i("相机未释放，继续等待");
                            handler.sendEmptyMessageDelayed(START_FACE_CHECK, 200);
                        }
                        break;
                    case START_FACE_CHECK1:
                        Log.e(TAG, "登录天翼RTC之后再重新打开人脸相机 " + RTC_AVAILABLE);
//                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 2000);
                        handler.sendEmptyMessageDelayed(START_FACE_CHECK, 2000);
                        break;
                    case START_FACE_CHECK2:
                        Log.e(TAG, "登录天翼RTC之前先释放相机");
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 0);//在登录天翼RTC之前先把人脸识别关掉
                        break;
                    case START_FACE_CHECK3:
                        Log.e(TAG, "登录天翼RTC之前先释放相机");
                        faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 0);//在登录天翼RTC之前先把人脸识别关掉
                        rtcTimer.schedule(new TimerTask() {
                            public void run() {
                                //用定时器控制2秒后再人脸识别，保证在人脸暂停后
                                Log.e(TAG, "人脸天翼登录状态有问题");
                                handler.sendEmptyMessage(START_FACE_CHECK);
                                DLLog.e(TAG, "人脸天翼登录状态有问题");
                            }
                        }, 3000);
//                        if (showMacText != null) {
//                            showMacText.setVisibility(View.VISIBLE);
//                            showMacText.setText("可视对讲设备未登录，请联系管理员");
//                        }
                        break;
                    case MSG_CARD_KEY_INCOM://卡及按键回调
                        ComBean comBean = (ComBean) msg.obj;
                        processData(comBean);//处理数据
                        break;
                    case MSG_CANCEL_ONVIDEO:
                        Log.e(TAG, "定时挂断");
                        sendMainMessager(MSG_DISCONNECT_VIEDO, "");
                        break;
                    default:
                        break;
                }

            }
        };
        mainMessage = new Messenger(handler);
    }

    protected void onAdvertiseImageChange(Object obj) {
        String source = (String) obj;
        source = HttpUtils.getLocalFileFromUrl(source);
        Bitmap bm = BitmapFactory.decodeFile(source);
        imageView.setImageBitmap(bm);
    }

    /**
     * 开门 :1卡2手机3人脸4邀请码5离线密码6临时密码'
     */
    private void onLockOpened(int typy) {
        control.openDoor();//小钴的开门方法没有回调接口，只发指令给硬件，没有成功与否的概念
        blockNo = "";
        setDialValue("");
        setTempkeyValue("");
        if (currentStatus != PASSWORD_MODE && currentStatus != PASSWORD_CHECKING_MODE) {
            setCurrentStatus(CALL_MODE);
        }
        String msg = "门开了";
        switch (typy) {
            case 1:
                msg = "刷卡开门成功";
                break;
            case 2:
                msg = "手机一键开门成功";
                break;
            case 3:
                msg = "人脸识别开门成功";
                break;
            case 4:
                //二维码暂无
                break;
            case 5:
                msg = "离线密码开门成功";
                break;
            case 6:
                msg = "临时密码开门成功";
                break;
            default:
                // Toast.makeText(this, "门开了", Toast.LENGTH_LONG).show();
                break;
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // TODO: 2018/6/13 通过延时发送限制人脸开门频率无用
//        identification = false;
//        if (faceHandler != null) {
//            faceHandler.removeMessages(MSG_FACE_DETECT_CHECK);
//            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CHECK, 10 * 1000);
//        }
    }

    /**
     * 初始化服务
     */
    private void initMainService() {
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);

        // TODO: 2018/9/5 没测 硬件不一样，参考newdoor项目
//        Intent dlIntent = new Intent(MainActivity.this, DoorLock.class);
//        startService(dlIntent);//start方式启动锁Service

        // TODO: 2018/8/10 注释
//        Intent msIntent = new Intent(MainActivity.this, MonitorService.class);
//        startService(msIntent);//start方式启动锁Service
    }

    /**
     * 服务连接监听
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            serviceMessage = new Messenger(service);
            Log.i(TAG, "连接MainService成功" + (serviceMessage != null));
            netWorkFlag = NetWorkUtils.isNetworkAvailable(MainActivity.this) ? 1 : 0;
            if (netWorkFlag == 0) {
//                enableReaderMode();//无网时打开读卡  注释：永远读卡
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "无网状态");
                        rl.setVisibility(View.VISIBLE);//界面上显示无网提示
                    }
                });
            } else {
                HttpApi.e("有网");
                setStatusBarIcon(true);
                // TODO: 2018/9/7   initSystemtime();
            }
            sendMainMessager(MAIN_ACTIVITY_INIT, NetWorkUtils.isNetworkAvailable(MainActivity.this));
            initNetListen();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * 获取版本名
     *
     * @return
     */
    private String getVersionName() {
        String verName = "";
        try {
            verName = this.getPackageManager().
                    getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            DLLog.e(TAG, "错误 getVersionName catch-> " + e.toString());
            e.printStackTrace();
        }
        return verName;
    }

/***********************************初始化一些基本东西end*****************************************/
    /**
     * 登录成功后
     *
     * @param msg
     */
    private void onLoginAfter(Message msg) {
        if (msg.obj != null) {
            NewDoorBean result = (NewDoorBean) msg.obj;
            sendMainMessager(MSG_RTC_REGISTER, null);
            //初始化社区信息
            setCommunityName(result.getXiangmu_name() == null ? "欣社区" : result.getXiangmu_name());
            setLockName(MainService.lockName);
            if ("C".equals(DeviceConfig.DEVICE_TYPE)) {//判断是否社区大门
                setDialStatus("请输入楼栋编号");
            }
            mac = result.getMac();

            sendMainMessager(REGISTER_ACTIVITY_DIAL, null);//开始心跳包

//            Log.e(TAG, "可以读卡");
//            enableReaderMode();//登录成功后开启读卡 注释：永远读卡

//            Log.e(TAG, "人脸94");
            //人脸识别开始  这里的人脸识别开始移到rtc登录成功以后
//            if (faceHandler != null) {
//                faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 1000);
//            }
        }
    }

    /**
     * 每隔十秒检查一次网络是否可用
     */
    private void initNetListen() {
        netTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int s = NetWorkUtils.isNetworkAvailable(MainActivity.this) ? 1 : 0;
                if (s != netWorkFlag) {//如果当前网络状态与之前不一致
                   /* if (s == 1) {//当前有网，之前没网
                        //关闭读卡
//                        disableReaderMode();//没网时打开过一次 注释：永远读卡
                        //时间更新
                        HttpApi.e("网络监测线程");
                        // TODO: 2018/9/7   initSystemtime();
                    } else {//当前没网，之前有网
//                        enableReaderMode(); //打开读卡 注释：永远读卡
                    }*/
                    sendMainMessager(MSG_UPDATE_NETWORKSTATE, s == 1 ? true : false);
                    netWorkFlag = s;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //主界面显示相关状态
                            if (netWorkFlag == 1) {
                                setStatusBarIcon(true);
                                rl.setVisibility(View.GONE);
                            } else {
                                setStatusBarIcon(false);
                                rl.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
//                if (mCamera == null) {
//                    Constant.RESTART_PHONE_OR_AUDIO = 2;
//                } else {
//                    Constant.RESTART_PHONE_OR_AUDIO = 0;
//                }
            }
        }, 500, 1000 * 10);
    }

    /**
     * 校时(校正本地时间)  之后放在心跳包接口里做
     */
    private void initSystemtime() {
        // TODO: 2018/9/5 没测
        HttpApi.e("开始校时 " + NetWorkUtils.isNetworkAvailable(this));
        if (NetWorkUtils.isNetworkAvailable(this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Calendar c = HttpApi.getInstance().loadTime();
                    if (c != null) {
                        if (checkTime(c)) {
                            SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd.HHmmss");
                            final String time = d.format(c.getTime());
                            // TODO: 2018/5/9 这里的校时要用到工控相关hwservice,暂时不注释,之后解决要重写
//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    String cmd = "date -s '[_update_time]'";
//                                    cmd = cmd.replace("[_update_time]", time);
//                                    ShellUtils.CommandResult result = InstallUtil.executeCmd(cmd);
//                                }
//                            }).start();
                            HttpApi.e("时间更新：" + time);
                        } else {
                            HttpApi.e("系统与服务器时间差小，不更新");
                        }
                    } else {
                        HttpApi.e("获取服务器时间出错！");
                    }
                }
            }).start();
        }
    }

    private boolean checkTime(Calendar c) {
        // TODO: 2018/9/5 没测
        Calendar c1 = Calendar.getInstance();
        long abs = Math.abs(c.getTimeInMillis() - c1.getTimeInMillis());
        if (abs > 1 * 60 * 1000) {
            return true;
        }
        return false;
    }

    /**
     * 通过ServiceMessenger将注册消息发送到Service中的Handler
     */
    private void sendMainMessager(int what, Object o) {
        Message message = Message.obtain();
        message.what = what;
        message.replyTo = mainMessage;
        message.obj = o;
        try {
            serviceMessage.send(message);
        } catch (RemoteException e) {
            DLLog.e(TAG, "错误 sendMainMessager catch-> " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 使用定时器,每隔5秒获得一次信号强度值
     */
    @SuppressLint("WifiManagerLeak")
    private void initNet() {
        wifiManager = (WifiManager) MainApplication.getApplication().getSystemService(WIFI_SERVICE);//获得WifiManager
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                switch (NetWorkUtils.getCurrentNetType(MainActivity.this)) {
                    case NETWORK_TYPE_WIFI:
                        //  LogDoor.i(TAG, "NETWORK_TYPE_WIFI");
                        wifiInfo = wifiManager.getConnectionInfo();
                        //获得信号强度值
                        level = wifiInfo.getRssi();
                        //根据获得的信号强度发送信息
                        if (level <= 0 && level >= -50) {
                            Message msg = new Message();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        } else if (level < -50 && level >= -70) {
                            Message msg = new Message();
                            msg.what = 2;
                            mHandler.sendMessage(msg);
                        } else if (level < -70 && level >= -80) {
                            Message msg = new Message();
                            msg.what = 3;
                            mHandler.sendMessage(msg);
                        } else if (level < -80 && level >= -100) {
                            Message msg = new Message();
                            msg.what = 4;
                            mHandler.sendMessage(msg);
                        } else {
                            Message msg = new Message();
                            msg.what = 5;
                            mHandler.sendMessage(msg);
                        }
                        break;
                    case NETWOKR_TYPE_ETHERNET:
                        Message msg = new Message();
                        msg.what = 11;
                        mHandler.sendMessage(msg);
//                        Log.i(TAG, "NETWOKR_TYPE_ETHERNET");
                        break;
                    case NETWOKR_TYPE_MOBILE:
                        Log.i(TAG, "gprs");
                        break;
                    case NETWORK_TYPE_NONE:
                        Log.i(TAG, "断网");
                    default:
                        break;
                }

            }
        }, 1000, 5000);
    }

    /**********************************************按键相关start***************************/
    //小钴按键响应由按键板响应，不走Android的按键响应方法

    private int lightIndex = 0;//是否亮灯的标志
    private CountDownTimer lightCloseTimer;
    private CountDownTimer lightOpenTimer;

    private void initLightTimer() {
        lightCloseTimer = new CountDownTimer(5000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.i("wh", "-------------------剩余" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                control.closeDeng();
                lightIndex = 0;
                Log.i("wh", "自动关灯 " + lightIndex);
            }
        };
        lightOpenTimer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
//                Log.i("wh", "-------------------剩余" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                control.openDeng();
                lightIndex = 1;
                Log.e("wh", "检测到有人,自动开灯 " + lightIndex);
            }
        };
    }

    /**
     * 初步处理卡和按键数据
     *
     * @param comBean
     */
    private void processData(ComBean comBean) {
        switch (comBean.index) {
            case 1:// 刷卡
                Log.e(TAG, "卡值为：" + comBean.sCard);
                onAccountReceived(comBean.sCard);
                break;
            case 2:// 按键
                Log.e(TAG, "按键为：" + comBean.sKey);
                int keyCode = convertKeyCode(comBean.sKey);
                Log.i(TAG, "按键key=" + keyCode + "模式currentStatus" + currentStatus);
                onKeyDown(keyCode);
                break;
            case 5:// 人体感应有人
                // TODO: 2018/10/29 设备不一样
                if (!cardRecord.checkLastBody("1") && lightIndex == 0) {
                    lightOpenTimer.cancel();
                    lightCloseTimer.cancel();
                    lightOpenTimer.start();
                    lightCloseTimer.start();
                }
                break;
            case 3:// 开门成功
//                sMsg.append("开门成功");
                break;
            case 4:// 继电器时间设置成功
//                sMsg.append("继电器时间设置成功");
                return;
        }
    }

    /**
     * 处理卡数据
     *
     * @param sCard
     */
    private void onAccountReceived(String sCard) {
        String account = StringUtils.reverseNum(sCard);//反转卡号

        Message message = Message.obtain();
        message.what = MSG_CARD_INCOME;
        message.obj = account;
        try {
            serviceMessage.send(message);
        } catch (RemoteException e) {
            DLLog.e(TAG, "错误 onAccountReceived catch-> " + e.toString());
            e.printStackTrace();
        }
    }

    private int convertKeyCode(String keyCode) {
        int value = -1;
        if (!TextUtils.isEmpty(keyCode)) {
            if (keyCode.equals("00")) {
                value = 0;
            } else if (keyCode.equals("01")) {
                value = 1;
            } else if (keyCode.equals("02")) {
                value = 2;
            } else if (keyCode.equals("03")) {
                value = 3;
            } else if (keyCode.equals("04")) {
                value = 4;
            } else if (keyCode.equals("05")) {
                value = 5;
            } else if (keyCode.equals("06")) {
                value = 6;
            } else if (keyCode.equals("07")) {
                value = 7;
            } else if (keyCode.equals("08")) {
                value = 8;
            } else if (keyCode.equals("09")) {
                value = 9;
            } else if (keyCode.equals("0D")) {
                value = DEVICE_KEYCODE_POUND;
            } else if (keyCode.equals("0F")) {
                value = DEVICE_KEYCODE_STAR;
            }
        }
        return value;
    }

    /**
     * 强制让控件获取焦点
     *
     * @param view
     */
    private void getFocus(View view) {
        view.setFocusable(true);//普通物理方式获取焦点
        view.setFocusableInTouchMode(true);//触摸模式获取焦点,不是触摸屏啊
        view.requestFocus();//要求获取焦点

        boolean focusable = view.isFocusable();
        Log.e(TAG, "获取焦点 " + focusable);
    }

    /**
     * 强制让控件失去焦点
     *
     * @param view
     */
    private void delFocus(View view) {
        view.setFocusable(false);//普通物理方式获取焦点
        view.setFocusableInTouchMode(false);//触摸模式获取焦点,不是触摸屏啊

        boolean focusable = view.isFocusable();
        Log.e(TAG, "失去焦点 " + focusable);
    }

    private String str;//输入框内容

    /**
     * 处理按键数据
     *
     * @param key
     */
    private void onKeyDown(int key) {
        Log.i(TAG, "默认按键key=" + key);
        if (nfcFlag) {
//            deleteFaceInfo(keyCode);//删除人脸信息
            //  inputCardInfo(keyCode);//录入卡片信息
        } else {
//            int key = convertKeyCode(keyCode);
            if (currentStatus == CALL_MODE || currentStatus == PASSWORD_MODE) {//处于呼叫模式或密码模式
                tv_input_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                str = tv_input_text.getText().toString();
                if (key == DEVICE_KEYCODE_POUND) {//确认键
                    if ("".equals(str)) {//输入框没值走切换模式
                        if (currentStatus == CALL_MODE) {//呼叫模式下，按确认键切换成密码模式
                            //密码模式
                            tv_input_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                            initPasswordStatus();
                        } else {
                            tv_input_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                            initDialStatus();
                        }
                    } else {//输入框有值走呼叫或密码
                        if (currentStatus == CALL_MODE) {//呼叫
//                            if (RTC_AVAILABLE) {
//                                if ("C".equals(DeviceConfig.DEVICE_TYPE)) {//大门
//                                    if (blockNo.length() == DeviceConfig.BLOCK_LENGTH) {
//                                        startDialing(blockNo);
//                                    } else if (blockNo.length() == DeviceConfig.MOBILE_NO_LENGTH) {//手机号
//                                        if (true) {//正则判断
//                                            startDialing(blockNo);
//                                        }
//                                    }
//                                } else {//单元
//                                    if (blockNo.length() == DeviceConfig.UNIT_NO_LENGTH) {
//                                        startDialing(blockNo);
//                                    } else if (blockNo.length() == DeviceConfig.MOBILE_NO_LENGTH) {//手机号
//                                        if (true) {//正则判断
//                                            startDialing(blockNo);
//                                        }
//                                    }
//                                }
//                            } else {
//                                Utils.DisplayToast(MainActivity.this, "可视对讲设备未登录，请稍后重试或重启应用");
//                            }

                            if ("C".equals(DeviceConfig.DEVICE_TYPE)) {//大门
                                if (blockNo.length() == DeviceConfig.BLOCK_LENGTH) {
                                    startDialing(blockNo);
                                } else if (blockNo.length() == DeviceConfig.MOBILE_NO_LENGTH) {//手机号
                                    if (true) {//正则判断
                                        startDialing(blockNo);
                                    }
                                }
                            } else {//单元
                                if (blockNo.length() == DeviceConfig.UNIT_NO_LENGTH) {
                                    startDialing(blockNo);
                                } else if (blockNo.length() == DeviceConfig.MOBILE_NO_LENGTH) {//手机号
                                    if (true) {//正则判断
                                        startDialing(blockNo);
                                    }
                                }
                            }
                        } else {//密码开门
                            if (guestPassword.length() == 6) {
                                checkPassword();
                            }
                        }
                    }
                } else if (key == DEVICE_KEYCODE_STAR) {//删除键
                    if (currentStatus == CALL_MODE) {
                        callInput();//房号或手机号删除一位
                    } else {
                        passwordInput();//密码删除一位
                    }
                    if (str == null || str.equals("")) {
                        //跳转到登录界面
//                        Intent intent = new Intent(this, InputCardInfoActivity.class);
//                        startActivityForResult(intent, INPUT_CARDINFO_REQUESTCODE);
                    }
                } else if (key >= 0) {//数字键
                    if (currentStatus == CALL_MODE) {
                        unitNoInput(key);
//                        callInput(key);
                    } else {
                        passwordInput(key);//密码开门
                    }
                }
            } else if (currentStatus == ERROR_MODE) {
                Utils.DisplayToast(MainActivity.this, "当前网络异常");
            } else if (currentStatus == CALLING_MODE) {//处于正在呼叫模式
                tv_input_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                Log.v(TAG, "onKeyDown-->111");
                if (key == KeyEvent.KEYCODE_STAR || key == DEVICE_KEYCODE_STAR) {
                    Log.v(TAG, "onKeyDown-->222");
                    startCancelCall();//取消呼叫
                }
            } else if (currentStatus == ONVIDEO_MODE) {
                tv_input_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                if (key == KeyEvent.KEYCODE_STAR || key == DEVICE_KEYCODE_STAR) {
                    sendMainMessager(MSG_DISCONNECT_VIEDO, "");
                }
            }
        }
    }

    /**********************************************按键相关end***************************/

    /**
     * 删除人脸信息
     *
     * @param keyCode
     */
    private void deleteFaceInfo(int keyCode) {
        //入口关闭
       /* String unit = et_unitno.getText().toString().trim();
        if (keyCode == DEVICE_KEYCODE_STAR) {//取消
            if (isFlag && TextUtils.isEmpty(unit)) {
                rl_nfc.setVisibility(View.GONE);
                nfcFlag = false;
                initDialStatus();
                isFlag = false;
                phone_face = "";
            } else if (isFlag && !TextUtils.isEmpty(unit)) {
                unit = backKey(unit);
                setTextValue(R.id.et_unitno, unit);
            }
        } else if (keyCode == DEVICE_KEYCODE_POUND) {//确认
            if (TextUtils.isEmpty(unit)) {
                Toast.makeText(this, "楼栋编号或者房屋编号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (unit.length() < 11) {
                Toast.makeText(this, "手机号长度不对", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isFlag && unit.length() == 11) {
                deleteFaceInfoThread(unit);
            }
        } else {
            int key = convertKeyCode(keyCode);
            if (key >= 0) {
                phone_face = phone_face + key;
                setTextValue(R.id.et_unitno, phone_face);
            }
        }*/
    }

    /**
     * 开启删除人脸信息线程
     *
     * @param phone 手机号
     */
    private void deleteFaceInfoThread(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean delete = ArcsoftManager.getInstance().mFaceDB.delete(phone);//删除

                Message message = Message.obtain();
                message.what = MSG_DELETE_FACE;
                message.obj = delete;
                handler.sendMessage(message);

                Log.e(TAG, "人脸信息删除" + "线程");
            }
        }).start();
    }

    void setTextValue(final int id, String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(id, thisValue);
            }
        });
    }

    /*********************************密码房号等输入状态相关start*******************************************/

    /**
     * 输入的房号或手机号的输入  1
     *
     * @param key
     */
    private void unitNoInput(int key) {
        blockNo = blockNo + key;
        if (blockNo.length() > 11) {
            blockNo = blockNo.substring(0, 11);
        }
        setDialValue(blockNo);
    }

    /**
     * 房号或手机号删除最后一位
     */
    private void callInput() {
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            if (blockId > 0) {
                if (blockNo.equals("")) {
                    blockId = 0;
                    blockNo = backKey(blockNo);
                    setDialStatus("请输入楼栋编号");
                    setDialValue(blockNo);
                } else {
                    blockNo = backKey(blockNo);
                    setDialValue(blockNo);
                }
            } else {
                blockNo = backKey(blockNo);
                setDialValue(blockNo);
            }
        } else {
            blockNo = backKey(blockNo);
            setDialValue(blockNo);
        }
    }

    /**
     * 密码输入
     *
     * @param key
     */
    private void passwordInput(int key) {
        guestPassword = guestPassword + key;
        if (guestPassword.length() > 6) {
            guestPassword = guestPassword.substring(0, 6);
        }
        setTempkeyValue(guestPassword);
    }

    /**
     * 密码 删除最后一位
     */
    private void passwordInput() {
        guestPassword = backKey(guestPassword);
        setTempkeyValue(guestPassword);
    }

    /**
     * 删除最后一位
     *
     * @param code
     * @return
     */
    private String backKey(String code) {
        if (code != null && code != "") {
            int length = code.length();
            if (length == 1) {
                code = "";
            } else {
                code = code.substring(0, (length - 1));
            }
        }
        return code;
    }

    private void checkPassword() {
        setCurrentStatus(PASSWORD_CHECKING_MODE);
        String thisPassword = guestPassword;
        guestPassword = "";
//        呼叫前，确认摄像头不被占用 虹软
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 0);
        }
        takePicture(thisPassword, false, this);
    }

    /**
     * 桌面显示呼叫模式
     */
    private void initDialStatus() {
        videoLayout.setVisibility(View.INVISIBLE);
        setCurrentStatus(CALL_MODE);
        blockNo = "";
        blockId = 0;
        if (DeviceConfig.DEVICE_TYPE.equals("C")) {
            setDialStatus("请输入楼栋编号");
        } else {
            setDialStatus("请输入房屋编号");
        }
        setDialValue(blockNo);
    }

    /**
     * 界面显示输入密码模式
     */
    private void initPasswordStatus() {
        stopPasswordTimeoutChecking();
        setDialStatus("请输入访客密码");
        videoLayout.setVisibility(View.INVISIBLE);
        setCurrentStatus(PASSWORD_MODE);
        guestPassword = "";
        setTempkeyValue(guestPassword);
        startTimeoutChecking();
    }

    /**
     * 密码验证后 是否成功等
     *
     * @param code
     */
    private void onPasswordCheck(ResponseBean code) {
        setCurrentStatus(PASSWORD_MODE);
        setTempkeyValue("");

        if (code != null) {
            if ("0".equals(code.getCode())) {
                Log.i(TAG, "临时密码验证成功");
            } else {
                if (code.getMsg() != null) {
                    Toast.makeText(MainActivity.this, code.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(MainActivity.this, "密码验证不成功", Toast.LENGTH_SHORT).show();
        }

//        if (faceHandler != null) {
//            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 3000);
//        }
        handler.sendEmptyMessageDelayed(START_FACE_CHECK, 3000);
    }

    /**
     * 离线密码验证后 是否成功等
     *
     * @param code
     */
    private void onCheckLixianPasswordAfter(boolean code) {
        setCurrentStatus(PASSWORD_MODE);
        setTempkeyValue("");
        if (code) {
            // Utils.DisplayToast(MainActivity.this, "您输入的密码验证成功");

        } else {
            Utils.DisplayToast(MainActivity.this, "密码验证不成功");
        }

//        if (faceHandler != null) {
//            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 3000);
//        }
        handler.sendEmptyMessageDelayed(START_FACE_CHECK, 3000);
//        handler.sendEmptyMessage(START_FACE_CHECK);
    }

    /**
     * 输入密码 无操作 启动自动跳转到输入房号的状态的线程
     */
    private void startTimeoutChecking() {
        passwordTimeoutThread = new Thread() {
            public void run() {
                try {
                    sleep(DeviceConfig.PASSWORD_WAIT_TIME); //等待指定的一个等待时间
                    if (!isInterrupted()) { //检查线程没有被停止
                        if (currentStatus == PASSWORD_MODE) { //如果现在是密码输入状态
                            if (TextUtils.isEmpty(guestPassword)) { //如果密码一直是空白的
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        initDialStatus();
                                    }
                                });
                                stopPasswordTimeoutChecking();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 startTimeoutChecking catch-> " + e.toString());
                    Log.e(TAG, "错误 startTimeoutChecking catch-> " + e.toString());
                    e.printStackTrace();
                }
                passwordTimeoutThread = null;
            }
        };
        passwordTimeoutThread.start();
    }

    /**
     * 输入密码 无操作 停止自动跳转到输入房号的状态的线程
     */
    protected void stopPasswordTimeoutChecking() {
        // TODO: 2018/9/1 此处线程终止的方法有风险，可用标志位或其他方法代替
        if (passwordTimeoutThread != null) {
            passwordTimeoutThread.interrupt();
            passwordTimeoutThread = null;
        }
    }

    /*********************************密码房号等输入状态相关end*******************************************/

    /****************************天翼rtc****************************/
    public void onRtcDisconnect() {
        Log.i(TAG, "重置房号为空 设置为 呼叫模式状态");
        blockNo = "";
        setDialValue(blockNo);
        setCurrentStatus(CALL_MODE);
        //启动广告
        advertiseHandler.start(adverErrorCallBack);

        handler.removeMessages(MSG_CANCEL_ONVIDEO);

        videoLayout.setVisibility(View.INVISIBLE);
        setVideoSurfaceVisibility(View.INVISIBLE);
    }

    public void onRtcConnected() {
        setCurrentStatus(ONVIDEO_MODE);
        setDialValue("");
//        暂时停止广告播放
        advertiseHandler.pause(adverErrorCallBack);
    }

    public void onRtcVideoOn() {
        setDialValue1("和" + blockNo + "通话中,挂断按取消键");
        initVideoViews();
        Log.e(TAG, "开始创建remoteView");
        if (mCamerarelease) {
            buildVideo();
        } else {
            cameraHandler.sendEmptyMessageDelayed(0x01, 200);
        }
        videoLayout.setVisibility(View.VISIBLE);
        setVideoSurfaceVisibility(View.VISIBLE);
    }

    private void buildVideo() {
        if (MainService.callConnection != null) {
            if (remoteView == null) {
                return;
            }
            MainService.callConnection.buildVideo(remoteView);//此处接听过快的会导致崩溃
        }

        Log.e(TAG, "开始定时挂断");
        // TODO: 2018/9/14 这里做一个1分钟的定时器，到时间后调用disconnectCallingConnection（）挂断通话
        handler.sendEmptyMessageDelayed(MSG_CANCEL_ONVIDEO, 1000 * 60);
    }

    /**
     * 创建本地view和远端
     */
    void initVideoViews() {
        //创建本地view
        if (localView != null) {
            return;
        }
        if (MainService.callConnection != null) {
            localView = (SurfaceView) MainService.callConnection.createVideoView(true, this, true);
        }
        if (localView != null) {
            localView.setVisibility(View.INVISIBLE);
            videoLayout.addView(localView);
            localView.setKeepScreenOn(true);
            localView.setZOrderMediaOverlay(true);
            localView.setZOrderOnTop(true);
        }
        //创建远端view
        if (MainService.callConnection != null) {
            remoteView = (SurfaceView) MainService.callConnection.createVideoView(false, this, true);
        }
        if (remoteView != null) {
            remoteView.setVisibility(View.INVISIBLE);
            remoteView.setKeepScreenOn(true);
            remoteView.setZOrderMediaOverlay(true);
            remoteView.setZOrderOnTop(true);
        }
    }

    /**
     * 显示本地view和远端view
     *
     * @param visible
     */
    private void setVideoSurfaceVisibility(int visible) {
        if (localView != null) {
            localView.setVisibility(visible);
        }
        if (remoteView != null) {
            remoteView.setVisibility(visible);
        }
    }

    /****************************天翼rtc****************************/

    /****************************拍照相关start************************/
    /**
     * 开始启动拍照
     */
    String curUrl;
    String faceOpenUrl;//人脸开门截图URL

    protected void takePicture(final String thisValue, final boolean isCall, final TakePictureCallback callback) {
        if (currentStatus == CALLING_MODE || currentStatus == PASSWORD_CHECKING_MODE) {
            final String uuid = getUUID(); //随机生成UUID
            lastImageUuid = uuid;
            setImageUuidAvaibale(uuid);
            //创建地址
            curUrl = "door/img/" + System.currentTimeMillis() + ".jpg";
            callback.beforeTakePickture(thisValue, curUrl, isCall, uuid);//校验房间号是否存在
            Log.v("MainActivity", "开始启动拍照");
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                        // interrupt() 以 “重新中断” 当前线程来完成
                        DLLog.e(TAG, "错误 takePicture catch-> " + e.toString());
                        Log.e(TAG, "错误 takePicture catch-> " + e.toString());
                        e.printStackTrace();
                    }
                    final String thisUuid = uuid;
                    if (checkTakePictureAvailable(thisUuid)) {
                        doTakePicture(thisValue, curUrl, isCall, uuid, callback);
                    } else {
                        Log.v("MainActivity", "取消拍照");
                    }
                }
            }.start();
        }
    }

    protected void takePictureFromPhone(final String imgUrl) {
        Log.v("MainActivity", "开始启动拍照");
        //启动人脸识别
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 0);
//            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 4000);打开相机放在拍照完成后
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 takePicture1 catch-> " + e.toString());
                    e.printStackTrace();
                }
                mCamerarelease = false;
                Log.v(TAG, "打开照相机");
                try {
                    camera = Camera.open(0);
                    Log.e(TAG, "打开照相机 3");
                } catch (Exception e) {
                    Log.e(TAG, "打开照相机 4" + e.toString());
                    DLLog.e(TAG, "错误 打开照相机 4 catch-> " + e.toString());
                }
                if (camera != null) {
                    try {
                        Camera.Parameters parameters = camera.getParameters();
                        parameters.setPreviewSize(320, 240);
                        camera.setParameters(parameters);
                        camera.setPreviewDisplay(autoCameraHolder);
                        camera.startPreview();
                        camera.autoFocus(null);
                        Log.v("MainActivity", "开始拍照");
                        camera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera1) {
                                try {
                                    Log.v("MainActivity", "释放照相机资源");
                                    Log.v("MainActivity", "拍照成功");
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    String fileurl = Environment.getExternalStorageDirectory() + "/" + LOCAL_IMG_PATH
                                            + "/" + System.currentTimeMillis() + ".jpg";
                                    final File file = new File(fileurl);
                                    File parentFile = file.getParentFile();
                                    if (!parentFile.exists()) {
                                        parentFile.mkdirs();
                                    }
                                    FileOutputStream outputStream = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                                    outputStream.close();
                                    final ImgFile imgFile = new ImgFile();
                                    imgFile.setImg_localurl(fileurl);
                                    imgFile.setImg_uploadurl(curUrl);
                                    if (camera != null) {
                                        camera.setPreviewCallback(null);
                                        camera.stopPreview();
                                        camera.release();
                                        camera = null;
                                        mCamerarelease = true;
                                        handler.sendEmptyMessage(START_FACE_CHECK);
                                    }
                                    OkHttpUtils.post().url(API.QINIU_IMG).build().execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            Log.i(TAG, "获取七牛token失败 e" + e.toString() + "手机一键开门七牛上传图片失败 保存照片信息到数据库");
                                            DbUtils.getInstans().insertOneImg(imgFile);
                                        }

                                        @Override
                                        public void onResponse(final String response, int id) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    String token = JsonUtil.getFieldValue(response, "data");
                                                    Log.i(TAG, "获取七牛token成功 开始上传照片  token" + token);
                                                    Log.e(TAG, "file七牛储存地址：" + imgUrl);
                                                    Log.e(TAG, "file本地地址：" + file.getPath() + "file大小" + file.length());

                                                    uploadManager.put(file.getPath(), imgUrl, token, new
                                                            UpCompletionHandler() {
                                                                @Override
                                                                public void complete(String key, ResponseInfo info,
                                                                                     JSONObject response) {
                                                                    if (info.isOK()) {
                                                                        Log.e(TAG, "手机一键开门七牛上传图片成功 +图片地址" + curUrl +
                                                                                "删除本地图片");
                                                                        file.delete();
                                                                    } else {
                                                                        Log.e(TAG, "手机一键开门七牛上传图片失败 保存照片信息到数据库");
                                                                        DbUtils.getInstans().insertOneImg(imgFile);
                                                                    }
                                                                    Log.e("七牛", "七牛info" + info.toString());
                                                                }
                                                            }, null);
                                                }
                                            }.start();
                                        }
                                    });

                                } catch (Exception e) {
                                    if (camera != null) {
//                                        camera.setPreviewCallback(null);
                                        camera.stopPreview();
                                        camera.release();
                                        camera = null;
                                        mCamerarelease = true;
                                        handler.sendEmptyMessage(START_FACE_CHECK);
                                    }
                                    Log.e(TAG, "打开照相机 5" + e.toString());
                                    DLLog.e(TAG, "错误 打开照相机 5 catch-> " + e.toString());
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        if (camera != null) {
//                            camera.setPreviewCallback(null);
                            camera.stopPreview();
                            camera.release();
                            camera = null;
                            mCamerarelease = true;
                            handler.sendEmptyMessage(START_FACE_CHECK);
                        }
                        Log.v("MainActivity", "照相出异常清除UUID");
                        DLLog.e(TAG, "错误 照相出异常清除UUID 5 catch-> " + e.toString());
                    }
                }
            }
        }.start();
    }

    /**
     * 七牛图片多文件上传
     */
    int curUploadImgIndex = 0;
    int curUploadImgIndexSuccess = 0;
    int curUploadImgIndexFail = 0;
    int uploadImgStatus = 0; //0没上传 1正在上传

    private void uploadImgs(final List<ImgFile> imgFiles) {
        // TODO: 2018/9/5 没测
        if (uploadImgStatus == 0) {
            //正在上传
            uploadImgStatus = 1;
            OkHttpUtils.post().url(API.QINIU_IMG).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.i("七牛", "onError七牛获取离线上传照片token 失败" + e.toString());
                    uploadImgStatus = 0;
                }

                @Override
                public void onResponse(String response, int id) {
                    final String token = JsonUtil.getFieldValue(response, "data");
//                    final String token = JsonUtil.getFieldValue(response, "uptoken");

                    Log.i("七牛", "获取七牛离线上传照片token成功 开始上传照片  token" + token);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            curUploadImgIndex = 0;
                            curUploadImgIndexFail = 0;
                            curUploadImgIndexSuccess = 0;
                            for (int i = 0; i < imgFiles.size(); i++) {
                                Log.i("七牛", "共有" + imgFiles.size() + "  开始传第  " + i + "  张图" + imgFiles.toString());
                                final ImgFile imgFile = imgFiles.get(i);
                                final File file = new File(imgFile.getImg_localurl());
                                if (!file.exists() || file.length() <= 0) {
                                    DbUtils.getInstans().deleteOneImg(imgFile);
                                    continue;
                                }
                                final String curUrl = imgFile.getImg_uploadurl();
                                uploadManager.put(imgFile.getImg_localurl(), curUrl, token, new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject res) {
                                        curUploadImgIndex++;
                                        if (info.isOK()) {
                                            curUploadImgIndexSuccess++;
                                            //当前图片上传成功
                                            //删除文件
                                            file.delete();
                                            //删除数据库中数据
                                            DbUtils.getInstans().deleteOneImg(imgFile);
                                            //判断图片是否都上传完成
                                            if ((curUploadImgIndex) == imgFiles.size()) {
                                                Log.i("七牛", curUploadImgIndex + "张上传完成\n" + curUploadImgIndexSuccess
                                                        + "张上传成功\n" + curUploadImgIndexFail + "张上传失败");
                                                uploadImgStatus = 0;
                                            }
                                        } else {
                                            if (info.statusCode == 614) {
                                                //表示文件已存在
                                                //删除文件
                                                file.delete();
                                                //删除数据库中数据
                                                DbUtils.getInstans().deleteOneImg(imgFile);
                                            }
                                            if ((curUploadImgIndex) == imgFiles.size()) {
                                                uploadImgStatus = 0;
                                            }
                                            //当前图片上传失败
                                            curUploadImgIndexFail++;
                                        }
                                        Log.e(TAG, "七牛info" + info.toString());
                                    }
                                }, null);
                            }
                        }
                    }).start();
                }
            });

        }
    }

    private synchronized void doTakePicture(final String thisValue, final String curUrl, final boolean isCall, final
    String uuid, final TakePictureCallback callback) {
        mCamerarelease = false;

        Log.v(TAG, "打开照相机");
        try {
            camera = Camera.open(0);
            Log.e(TAG, "打开照相机 3");
        } catch (Exception e) {
            Log.e(TAG, "打开照相机 5" + e.toString());
            e.printStackTrace();
            DLLog.e(TAG, "错误 doTakePicture 打开照相机 5 catch-> " + e.toString());
        }
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(320, 240);
                // TODO: 2018/10/9 这里要设置图片分辨率吗？摄像头支持吗？
//                parameters.setPictureSize(1280, 720);
                camera.setParameters(parameters);
                camera.setPreviewDisplay(autoCameraHolder);
                camera.startPreview();
                camera.autoFocus(null);
                Log.v("MainActivity", "开始拍照");
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera1) {
                        try {
                            if (camera != null) {
                                camera.setPreviewCallback(null);
                                camera.stopPreview();
                                camera.release();
                                camera = null;
                                mCamerarelease = true;
                            }
                            Log.v("MainActivity", "释放照相机资源");
                            Log.v("MainActivity", "拍照成功");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            String fileurl = Environment.getExternalStorageDirectory() + "/" + LOCAL_IMG_PATH + "/" +
                                    System.currentTimeMillis() + ".jpg";
                            final File file = new File(fileurl);
                            File parentFile = file.getParentFile();
                            if (!parentFile.exists()) {
                                parentFile.mkdirs();
                            }
                            FileOutputStream outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                            outputStream.close();
                            final ImgFile imgFile = new ImgFile();
                            imgFile.setImg_localurl(fileurl);
                            imgFile.setImg_uploadurl(curUrl);
                            if (checkTakePictureAvailable(uuid)) {
                                OkHttpUtils.post().url(API.QINIU_IMG).build().execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.i(TAG, "获取七牛token失败 e" + e.toString() + "保存照片信息到数据库");
                                        DbUtils.getInstans().insertOneImg(imgFile);
                                    }

                                    @Override
                                    public void onResponse(final String response, int id) {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                String token = JsonUtil.getFieldValue(response, "data");
                                                Log.i(TAG, "获取七牛token成功 开始上传照片  token" + token);
                                                Log.e(TAG, "file七牛储存地址：" + curUrl);
                                                Log.e(TAG, "file本地地址：" + file.getPath() + "file大小" + file.length());

                                                uploadManager.put(file.getPath(), curUrl, token, new
                                                        UpCompletionHandler() {
                                                            @Override
                                                            public void complete(String key, ResponseInfo info,
                                                                                 JSONObject
                                                                                         response) {
                                                                if (info.isOK()) {
                                                                    Log.e(TAG, "七牛上传图片成功 删除本地图片");
                                                                    if (file != null) {
                                                                        file.delete();
                                                                    }
                                                                } else {
                                                                    Log.e(TAG, "七牛上传图片失败 保存照片信息到数据库");
                                                                    DbUtils.getInstans().insertOneImg(imgFile);
                                                                }
                                                                if (checkTakePictureAvailable(uuid) && info.isOK() &&
                                                                        isCall) {
                                                                    Log.i(TAG, "开始发送图片到手机显示照片");
                                                                    callback.afterTakePickture(thisValue, curUrl,
                                                                            isCall, uuid);
                                                                } else {
                                                                    Log.v("MainActivity", "上传照片成功不发送到手机,但已取消");
                                                                }
                                                                clearImageUuidAvaible(uuid);
                                                                Log.v(TAG, "正常清除" + uuid);
                                                                Log.e(TAG, "七牛info" + info.toString());
                                                            }
                                                        }, null);
                                            }
                                        }.start();
                                    }
                                });
                            } else {
                                Log.v("MainActivity", "拍照成功，但已取消");
                            }
                        } catch (Exception e) {
                            if (camera != null) {
//                                camera.setPreviewCallback(null);
                                camera.stopPreview();
                                camera.release();
                                camera = null;
                                mCamerarelease = true;
                            }
                            Log.e(TAG, "打开照相机 5" + e.toString());
                            DLLog.e(TAG, "错误 doTakePicture 5 catch-> " + e.toString());
                            e.printStackTrace();

                        }
                    }
                });
            } catch (Exception e) {
                // TODO: 2018/8/28 这里不知道要不要加 callback.afterTakePickture(thisValue, null, isCall, uuid);
                if (camera != null) {
//                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    mCamerarelease = true;
                }
                DLLog.e(TAG, "错误 doTakePicture 4 catch-> " + e.toString());
                Log.v("MainActivity", "照相出异常清除UUID");
                clearImageUuidAvaible(uuid);
            }
        }
    }

    @Override
    public void beforeTakePickture(String thisValue, String curUrl, boolean isCall, String uuid) {
        startDialorPasswordDirectly(thisValue, curUrl, isCall, uuid);
    }

    @Override
    public void afterTakePickture(String thisValue, String fileUrl, boolean isCall, String uuid) {
        startSendPictureDirectly(thisValue, fileUrl, isCall, uuid);
    }

    private boolean checkTakePictureAvailable(String uuid) {
        String thisValue = uuidMaps.get(uuid);
        boolean result = false;
        if (thisValue != null && thisValue.equals("Y")) {
            result = true;
        }
        Log.v(TAG, "检查UUID" + uuid + result);
        return result;
    }

    private String getUUID() {
        UUID uuid = UUID.randomUUID();
        String result = UUID.randomUUID().toString();
        return result;
    }

    private void setImageUuidAvaibale(String uuid) {
        Log.v("MainActivity", "加入UUID" + uuid);
        uuidMaps.put(uuid, "Y");
    }

    private void clearImageUuidAvaible(String uuid) {
        Log.v("MainActivity", "清除UUID" + uuid);
        uuidMaps.remove(uuid);
    }

    /****************************拍照相关end************************/

    /****************************呼叫相关start************************/
    /**
     * 呼叫出现错误
     *
     * @param reason
     */
    protected void onCallMemberError(int reason) {
        blockNo = "";
        setDialValue("");
        setCurrentStatus(CALL_MODE);
        if (reason == MSG_CALLMEMBER_ERROR) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号错误或者无注册用户");
            Log.v("MainActivity", "无用户取消呼叫");
            clearImageUuidAvaible(lastImageUuid);
        } else if (reason == MSG_CALLMEMBER_TIMEOUT) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号无人应答");
        } else if (reason == MSG_CALLMEMBER_SERVER_ERROR) {
            Utils.DisplayToast(MainActivity.this, "无法从服务器获取住户信息，请联系管理处");
        }
//        else if (reason == MSG_CALLMEMBER_DIRECT_TIMEOUT) {
//            Utils.DisplayToast(MainActivity.this, "您呼叫的房间直拨电话无人应答");
//        }
        else if (reason == MSG_CALLMEMBER_NO_ONLINE) {
            Utils.DisplayToast(MainActivity.this, "您呼叫的房间号无人在线");
        }


        //启动人脸识别
//        if (faceHandler != null) {
//            Log.e(TAG, "人脸99");
//            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 3000);
//        }
        handler.sendEmptyMessageDelayed(START_FACE_CHECK, 3000);
    }

    /**
     * 删除键取消拨号
     */
    protected void startCancelCall() {
        new Thread() {
            @Override
            public void run() {
                //  stopCallCamera();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 startCancelCall catch-> " + e.toString());
                    Log.e(TAG, "错误 startCancelCall catch-> " + e.toString());
                    e.printStackTrace();
                }

                sendMainMessager(MSG_CANCEL_CALL, "");
//                if (faceHandler != null) {
//                    faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 1000);
//                }
                handler.sendEmptyMessageDelayed(START_FACE_CHECK, 1000);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO: 2018/9/29 不要捕捉后只记录不处理，这样相当于生吞中断，应该保留中断发生的证据，以便调用栈中更高层的代码能知道中断，并对中断作出响应。该任务可以通过调用
                    // interrupt() 以 “重新中断” 当前线程来完成
                    DLLog.e(TAG, "错误 startCancelCall catch-> " + e.toString());
                    e.printStackTrace();
                }
                toast("您已经取消拨号");
                resetDial();
            }
        }.start();
    }

    /**
     * 开始呼叫
     *
     * @param blockNo
     */
    private void startDialing(String blockNo) {

        if (blockNo.equals("9991") || blockNo.equals("99999991")) {
            DLLog.e(TAG, "手动重启");
            onReStartVideo();
            return;
        }

//        if (blockNo.equals("9992") || blockNo.equals("99999992")) {
//            File file = DLLog.upLoadFile();
//            if (null != file) {
//                uploadLogToQiNiu(file);
//            } else {
//                DLLog.d("上传日志七牛", "没有日志");
//            }
//            return;
//        }

        if (blockNo.equals("9993") || blockNo.equals("99999993")) {
//            sendMainMessager(MSG_TONGJI_VEDIO1, null);
//            changeAppBrightness(50);
//            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 100);
//            boolean delete = ArcsoftManager.getInstance().mFaceDB.delete1("15273288531");//删除
//            Log.e(TAG, "人脸更新 遍历删除集合，删除人脸信息 " + "15273288531" + " " + delete);
//            facetimer.schedule(new TimerTask() {
//                public void run() {
//                    //execute the task
//                    //删除单个人脸时处理速度可能过快，导致先人脸识别再人脸暂停，所以用定时器控制1秒后再人脸识别，保证在人脸暂停后
//                    handler.sendEmptyMessage(START_FACE_CHECK);
//                }
//            }, 2000);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    String cmd = "pm install -r " + "/mnt/internal_sd/myapk/door.apk";
//                    HttpApi.i("UpdateService安装命令：" + cmd);
//                    //以下两个方法应该等效
//                    ShellUtils.CommandResult result = InstallUtil.executeCmd(cmd);
////                ShellUtils.CommandResult commandResult = ShellUtils.execCommand(cmd, false);
//                    HttpApi.i("UpdateService安装结果：" + result.toString());
//                }
//            }).start();
//            SoundPoolUtil.getSoundPoolUtil().loadVoice(getBaseContext(), 011112);
//            if (weituoDialog == null) {
//                Log.e("DialogUtil", "创建弹框");
//                weituoDialog = DialogUtil.showBottomDialog(MainActivity.this);
//            }
//            if (!weituoDialog.isShowing()) {
//                weituoDialog.show();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        weituoDialog.dismiss();
//                    }
//                }, 1000);
//            }

            if (null != ArcsoftManager.getInstance().mFaceDB.mRegister1 && ArcsoftManager.getInstance().mFaceDB
                    .mRegister1.size() > 0) {
                Toast.makeText(MainActivity.this, "当前人脸数" + ArcsoftManager.getInstance().mFaceDB.mRegister1.size(),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "当前人脸数 0", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (RTC_AVAILABLE) {
            //呼叫前，确认摄像头不被占用 虹软
            if (faceHandler != null) {
                faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 0);
            }

            Log.i(TAG, "拍摄访客照片 并进行呼叫" + blockNo);
            setCurrentStatus(CALLING_MODE);
            //拍摄访客照片 并进行呼叫
            takePicture(blockNo, true, MainActivity.this);
        } else {
            Utils.DisplayToast(MainActivity.this, "可视对讲设备未登录，请稍后重试或重启应用");
            return;
        }
    }

    /**
     * 开始呼叫
     */
    protected void startDialorPasswordDirectly(final String thisValue, final String fileUrl, final boolean isCall,
                                               String uuid) {
        if (currentStatus == CALLING_MODE || currentStatus == PASSWORD_CHECKING_MODE) {
            Message message = Message.obtain();
            String[] parameters = new String[3];
            if (isCall) {
                setDialValue1("呼叫" + thisValue + "，取消请按删除键");
                message.what = MSG_START_DIAL;
                if (DeviceConfig.DEVICE_TYPE.equals("C")) {
                    parameters[0] = thisValue;
                } else {
                    parameters[0] = thisValue;
                }
            } else {
                tv_input_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                setTempkeyValue("准备验证密码" + thisValue + "...");
                message.what = MSG_CHECK_PASSWORD;
                parameters[0] = thisValue;
            }
            parameters[1] = fileUrl;
            parameters[2] = uuid;
            message.obj = parameters;
            try {
                serviceMessage.send(message);
            } catch (RemoteException er) {
                DLLog.e(TAG, "错误 开始呼叫 catch-> " + er.toString());
                er.printStackTrace();
            }
        }
    }

    /**
     * 发送访客图片地址
     *
     * @param thisValue
     * @param fileUrl
     * @param isCall
     * @param uuid
     */
    protected void startSendPictureDirectly(final String thisValue, final String fileUrl, final boolean isCall,
                                            String uuid) {
        if (fileUrl == null || fileUrl.length() == 0) {
            return;
        }
        Message message = Message.obtain();
        if (isCall) {
            message.what = MSG_START_DIAL_PICTURE;
        } else {
            // message.what = MainService.MSG_CHECK_PASSWORD_PICTURE;
        }
        String[] parameters = new String[3];
        parameters[0] = thisValue;
        parameters[1] = fileUrl;
        parameters[2] = uuid;
        message.obj = parameters;
        try {
            serviceMessage.send(message);
        } catch (RemoteException er) {
            DLLog.e(TAG, "错误 发送访客图片地址 catch-> " + er.toString());
            er.printStackTrace();
        }
    }

    /****************************呼叫相关end************************/

    /****************************设置一些状态start************************/
    synchronized void setCurrentStatus(int status) {
        currentStatus = status;
    }

    private void setDialStatus(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_label, thisValue);
            }
        });
    }

    //设置桌面会话的状态
    private void setDialValue(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_text, thisValue);
            }
        });
    }

    private void setDialValue1(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                tv_input_text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                setTextView(R.id.tv_input_text, thisValue);
            }
        });
    }

    /**
     * 设置自定义状态栏的状态（WiFi标志）
     *
     * @param state true 显示  false 隐藏
     */
    private void setStatusBarIcon(boolean state) {
        if (state) {
            //显示
            if (wifi_image.getVisibility() == View.INVISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi_image.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            //隐藏
            if (wifi_image.getVisibility() == View.VISIBLE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        wifi_image.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    private void toast(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Utils.DisplayToast(MainActivity.this, message);
            }
        });
    }

    private void setTempkeyValue(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTextView(R.id.tv_input_text, thisValue);
            }
        });
    }

    private void setTextView(int id, String txt) {
        ((TextView) findViewById(id)).setText(txt);
    }

    /**
     * 设置社区名字
     *
     * @param value
     */
    private void setCommunityName(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "设置社区" + thisValue);
                setTextView(R.id.tv_community, thisValue);
            }
        });
    }

    /**
     * 设置锁的名字
     *
     * @param value
     */
    private void setLockName(String value) {
        final String thisValue = value;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "设置单元" + thisValue);
                setTextView(R.id.tv_lock, thisValue);
            }
        });
    }

    private void setTongGaoInfo() {
        if (isTongGaoStart()) {
            if (null != noticeBeanList && noticeBeanList.size() > 0) {//通告列表有数据
                currentNoticeBean = noticeBeanList.get(tongGaoIndex);
                tongGaoIndex++;
                if (tongGaoIndex == noticeBeanList.size()) {//循环一遍以后，重置游标
                    tongGaoIndex = 0;
                }
            } else {//通告列表无数据
                currentNoticeBean = defaultNotice;
            }
            Log.e(TAG, "设置通告 currentNoticeBean" + currentNoticeBean.toString());
            Log.e(TAG, "设置通告 过期时间 " + currentNoticeBean.getShixiao_shijian() + " 当前时间 " + StringUtils
                    .transferLongToDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
            Log.e(TAG, "设置通告 过期时间 " + StringUtils.transferDateToLong(currentNoticeBean.getShixiao_shijian()) + " " +
                    "当前时间" + " " + System.currentTimeMillis());
            if (Long.parseLong(StringUtils.transferDateToLong(currentNoticeBean.getShixiao_shijian())) > System
                    .currentTimeMillis()) {//过期时间大于当前时间
                Log.e(TAG, "设置通告 有数据");
                if (Long.parseLong(StringUtils.transferDateToLong(currentNoticeBean.getKaishi_shijian())) < System
                        .currentTimeMillis()) {//开始时间小于当前时间，可以显示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            setTextView(R.id.gonggao, currentNoticeBean.getNeirong());
                            tv_gonggao_title.setText(currentNoticeBean.getBiaoti());
                            tv_gonggao.setText(currentNoticeBean.getNeirong());
                            as.setScrolled(false);//关闭自动滚动
                            as.change();//位置重置
                            as.setScrolled(true);//打开自动滚动
                            as.autoScroll(isTongGaoFrist);//开启自动滚动
//                            tv_gonggao.setText(currentNoticeBean.getNeirong());
                        }
                    });
                } else {//开始时间大于当前时间，跳过，直接显示下一条
                    setTongGaoInfo();
                }
            } else {
                tongGaoIndex--;
                if (tongGaoIndex == -1) {
                    Log.e(TAG, "通告清零");
                    noticeBeanList.clear();
                    noticeBeanList = null;
                } else {
                    Log.e(TAG, "移除一条通告");
                    noticeBeanList.remove(tongGaoIndex);
                }
                setTongGaoInfo();
            }
        } else {
            currentNoticeBean = defaultNotice;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_gonggao_title.setText(currentNoticeBean.getBiaoti());
                    tv_gonggao.setText(currentNoticeBean.getNeirong());
//                    setTextView(R.id.gonggao_title, currentNoticeBean.getBiaoti());
//                    setTextView(R.id.gonggao, currentNoticeBean.getNeirong());
                    as.setScrolled(false);//关闭自动滚动
                    as.change();//位置重置
                }
            });
        }
    }

    private boolean isTongGaoStart() {
        Log.e(TAG, "开始通告判断");
        boolean b = false;
        if (null != noticeBeanList && noticeBeanList.size() > 0) {
            for (NoticeBean noticeBean : noticeBeanList) {
                if (Long.parseLong(StringUtils.transferDateToLong(noticeBean.getKaishi_shijian())) < System
                        .currentTimeMillis()) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }

    /**
     * 设置mac地址显示等
     *
     * @param mac
     */
    private void showMacaddress(String mac) {
        if (showMacText != null && mac != null && mac.length() > 0) {
            showMacText.setVisibility(View.VISIBLE);
            showMacText.setText("MAC地址未注册，请添加\nMac地址：" + mac);
        }
    }

    /**
     * 取消呼叫设置状态
     */
    protected void resetDial() {
        blockNo = "";
        setDialValue(blockNo);
        setCurrentStatus(CALL_MODE);
    }

    /****************************设置一些状态end************************/

    /**
     * 使用Handler实现UI线程与Timer线程之间的信息传递,每5秒告诉UI线程获得wifi Info
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 如果收到正确的消息就获取WifiInfo，改变图片并显示信号强度
                // TODO: 2018/5/8 以下QQ物联相关暂时注释
                case 11:
                    wifi_image.setImageResource(R.mipmap.ethernet);
                    break;
                case 1:
                    wifi_image.setImageResource(R.mipmap.wifi02);
                    break;
                case 2:
                    wifi_image.setImageResource(R.mipmap.wifi02);
                    break;
                case 3:
                    wifi_image.setImageResource(R.mipmap.wifi03);
                    break;
                case 4:
                    wifi_image.setImageResource(R.mipmap.wifi04);
                    break;
                case 5:
                    wifi_image.setImageResource(R.mipmap.wifi05);
                    break;
                case 6://无网络连接
                    rl.setVisibility(View.VISIBLE);
                    break;
                case 7:
                    //提示用户无网络连接
                    rl.setVisibility(View.GONE);
                    break;
                default:
                    //以防万一
                    wifi_image.setImageResource(R.mipmap.wifi_05);
                    rl.setVisibility(View.VISIBLE);
            }
        }
    };

    /****************************点击事件相关start**************************************/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {//跳转网络或网络设置
            case R.id.net_view_rl: {
                Intenet.system_set(MainActivity.this, INPUT_SYSTEMSET_REQUESTCODE);
                break;
            }
            case R.id.iv_setting: {
                initMenu();//初始化左上角弹出框
                break;
            }
        }
    }

    String sd = null;

    /**
     * 初始化左上角弹出框
     */
    private void initMenu() {
        PopupMenu popup = new PopupMenu(MainActivity.this, iv_setting);
        popup.getMenuInflater().inflate(R.menu.poupup_menu_home, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings1:
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);//跳轉到系統設置
                        intent.putExtra("back", true);
                        startActivityForResult(intent, INPUT_SYSTEMSET_REQUESTCODE);
                        break;
                    case R.id.action_catIP:
                        Log.e(TAG, "menu 本机的IP");
                        Toast.makeText(MainActivity.this, "本机的IP：" + Intenet.getHostIP(), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.action_catVersion:
                        Log.e(TAG, "menu 本机的固件版本");
//                        Toast.makeText(MainActivity.this, "本机的固件版本：" + hwservice.getSdkVersion(),Toast.LENGTH_LONG)
// .show();
                        String s = sd;
                        Log.e(TAG, s);
                        break;
                    case R.id.action_updateVersion:
                        Log.e(TAG, "menu 更新");
//                        onReStartVideo();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                tv_input_text.setText("sdflkjdsl");
                            }
                        }).start();
                        break;
                    case R.id.action_settings3://上传日志
//                        clearMemory();
                        Log.e(TAG, "menu 上传日志");
                        break;
                    case R.id.action_settings7://重启
                        Log.e(TAG, "menu 重启");
                        Intent intent1 = new Intent(Intent.ACTION_REBOOT);
                        intent1.putExtra("nowait", 1);
                        intent1.putExtra("interval", 1);
                        intent1.putExtra("window", 0);
                        sendBroadcast(intent1);
//                        DoorLock.getInstance().runReboot();
                        break;
                    case R.id.action_settings10://退出
                        // TODO: 2018/9/5 没测
                        setResult(RESULT_OK);
                        MainActivity.this.stopService(new Intent(MainActivity.this, MainService.class));
                        finish();
                        sendBroadcast(new Intent("com.android.action.display_navigationbar"));
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
        popup.show();
    }
    /****************************点击事件相关end**************************************/

    /****************************虹软相关start*********************************************/

    /**
     * 初始化人脸相关与身份证识别
     */
    private void initFaceDetectAndIDCard() {
        //获取屏幕大小
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        Log.v(TAG, "initFaceDetect-->" + mWidth + "/" + mHeight + "/" + density);

        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);//mCameraMirror=true:Y轴镜像  180:旋转180度
        mSurfaceView.debug_print_fps(true, false);

        //人脸跟踪初始化引擎，设置检测角度、范围，数量。创建对象后，必须先于其他成员函数调用，否则其他成员函数会返回 MERR_BAD_STATE
        //orientsPriority 指定检测的角度 scale 指定支持检测的最小人脸尺寸(16) maxFaceNum 最多能检测到的人脸个数(5)
        AFT_FSDKError err = engine.AFT_FSDK_InitialFaceEngine(arc_appid, ft_key, AFT_FSDKEngine.AFT_OPF_0_HIGHER_EXT,
                16, 5);
        Log.d(TAG, "AFT_FSDK_InitialFaceEngine =" + err.getCode());
        err = engine.AFT_FSDK_GetVersion(version);//获取版本信息
        Log.d(TAG, "AFT_FSDK_GetVersion:" + version.toString() + "," + err.getCode());

        faceHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Log.v(TAG, "face" + "handleMessage-->" + msg.what + "/" + Thread.currentThread().getName());
                switch (msg.what) {
                    case MSG_FACE_DETECT_CHECK://门开了以后identification设为false，发送此消息MSG_FACE_DETECT_CHECK
                        identification = true;
//                        idOperation =true;
                        break;
                    case MSG_FACE_DETECT_INPUT://人脸识别录入(拿到网络图片后发出人脸识别暂停，然后发出录入消息)
                        Log.e(TAG, "人脸识别录入");
                        // TODO: 2018/5/11  这里要传入整个网络图片的所有地址过来给faceDetectInput方法使用
                        faceDetectInput();
                        break;
                    case MSG_FACE_DETECT_CONTRAST://人脸识别对比
                        Log.e(TAG, "人脸识别对比");
                        identification = true;
                        if (mFRAbsLoop != null) {
                            mFRAbsLoop.resumeThread();
                        }
                        if (mSurfaceView.getVisibility() != View.VISIBLE) {
                            mGLSurfaceView.setVisibility(View.VISIBLE);
                            mSurfaceView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MSG_FACE_DETECT_PAUSE://人脸识别暂停
                        Log.e(TAG, "人脸识别暂停" + "开始照相机");
                        handler.removeMessages(START_FACE_CHECK);
                        faceHandler.removeMessages(MSG_FACE_DETECT_CONTRAST);
                        //这个地方可能因为删除人脸的流程过快造成先人脸识别对比再人脸识别暂停，以至于无法进行人脸识别
                        identification = false;
                        if (mFRAbsLoop != null) {
                            mFRAbsLoop.pauseThread();
                        }
                        if (mSurfaceView.getVisibility() != View.GONE) {
                            mGLSurfaceView.setVisibility(View.GONE);
                            mSurfaceView.setVisibility(View.GONE);
                        }
                        break;
                }
                return false;
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.v("人脸识别", "initFaceDetect-->" + 111);
//                boolean b = ArcsoftManager.getInstance().mFaceDB.loadFaces();
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (ArcsoftManager.getInstance().mFaceDB.mRegister.isEmpty()) {
////                            Log.v("人脸识别", "initFaceDetect-->" + 333);
//                            Utils.DisplayToast(MainActivity.this, "没有注册人脸，请先注册");
//                            return;
//                        }
//                        identification = true;
//                        Log.v("人脸识别", "initFaceDetect-->人脸数据加载完成 " + ArcsoftManager.getInstance().mFaceDB
//                                .mRegister.size());
//                        Utils.DisplayToast(MainActivity.this, "人脸数据加载完成" + ArcsoftManager.getInstance().mFaceDB
//                                .mRegister.size());
//                    }
//                });
//            }
//        }).start();
        Log.v("人脸识别", "initFaceDetect-->" + 111);
        mFRAbsLoop = new FRAbsLoop();
        mFRAbsLoop.start();
    }

    /**
     * 执行顺序setupCamera-startPreviewLater-setupChanged-onPreview-onBeforeRender-onPreview
     * -onAfterRender
     * -onBeforeRender-onPreview-onAfterRender...
     */

    /**
     * SurfaceView被创建起来的时候，会被触发，需要在这个监听接口中，把相机打开并且设置参数，但不用启动预览
     *
     * @return
     */
    @Override
    public Camera setupCamera() {
//        Log.e(TAG, "相机" + "setupCamera");

        try {//这里其实不用捕捉错误
            // TODO: 2018/10/9 打开摄像头可以用Camera.CameraInfo.CAMERA_FACING_BACK来判断，但是双目的直接用后置相机


            try {
                mCamera = Camera.open(0);//打开后置相机
                Log.e(TAG, "相机 ID为0");
            } catch (Exception e) {
                mCamera = null;
                DLLog.e("错误 摄像头 MainActivity", "主页面 相机打开失败" + " setupCamera-->" + e.toString());
                Constant.RESTART_PHONE_OR_AUDIO = 1;
                e.printStackTrace();
            }


            // TODO: 2018/10/9 设置尺寸和指定图像的格式用下面两个方法试试
            // setPreviewSizes(); //设置预览分辨率
            //setPreviewFormat(); //设置NV21

            if (null != mCamera) {
                try {
                    // TODO: 2018/10/9  有最优尺寸？
                    Camera.Parameters parameters = mCamera.getParameters();
                    if (null != parameters) {
                        parameters.setPreviewSize(640, 480);//设置尺寸
                        mCamera.setParameters(parameters);
                    }
                } catch (Exception e) {
                    DLLog.e("摄像头 MainActivity", "错误 主页面 分辨率" + " setupCamera-->" + e.toString());
                    Constant.RESTART_PHONE_OR_AUDIO = 1;
                    e.printStackTrace();
                }
            }

            if (null != mCamera) {
                try {
                    Camera.Parameters parameters1 = mCamera.getParameters();
                    if (null != parameters1) {
                        parameters1.setPreviewFormat(ImageFormat.NV21);//指定图像的格式
                        // (NV21：是一种YUV420SP格式，紧跟Y平面的是VU交替的平面)
                        mCamera.setParameters(parameters1);
                    }
                } catch (Exception e) {
                    DLLog.e("摄像头 MainActivity", "错误 主页面 图像格式" + " setupCamera-->" + e.toString());
                    Constant.RESTART_PHONE_OR_AUDIO = 1;
                    e.printStackTrace();
                }
            }

            if (null == mCamera) {
                Constant.RESTART_PHONE_OR_AUDIO = 1;
            }
//            for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
//                LogDoor.v(TAG, "SIZE:" + size.width + "x" + size.height);
//            }
//            for (Integer format : parameters.getSupportedPreviewFormats()) {
//                Log.v(TAG, "相机FORMAT:" + format);
//            }
////
//            List<int[]> fps = parameters.getSupportedPreviewFpsRange();
//            for (int[] count : fps) {
//                Log.d(TAG, "相机T:");
//                for (int data : count) {
//                    Log.d(TAG, "相机V=" + data);
//                }
//            }
            //parameters.setPreviewFpsRange(15000, 30000);
            //parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
            //parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            //parameters.setAntibanding(Camera.Parameters.ANTIBANDING_AUTO);
            //parmeters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            //parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
        } catch (Exception e) {
            DLLog.e("摄像头 MainActivity", "错误 主页面 " + e.toString() + " setupCamera-->" + e.getMessage());
//            onReStartVideo();
            Constant.RESTART_PHONE_OR_AUDIO = 1;
            e.printStackTrace();
//            Log.v(TAG, "setupCamera-->" + e.getMessage());
        }

        if (mCamera != null) {
            mWidth = mCamera.getParameters().getPreviewSize().width;
            mHeight = mCamera.getParameters().getPreviewSize().height;
            mCamera.autoFocus(null);
            Log.v(TAG, "SIZE:" + mWidth + "x" + mHeight);//800x600 与设置值一样
        }
        return mCamera;
    }

    private void setPreviewSizes() {
        int pWidht = 640;
        int pHeight = 480;
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                parameters.setPreviewSize(pWidht, pHeight);
                mCamera.setParameters(parameters);
                HttpApi.i("设置预览宽高完成");
            }
        } catch (Exception e) {
            HttpApi.i("设置预览宽高失败");
            e.printStackTrace();
        }

//        if (null != mCamera) {
//            try {
//                // TODO: 2018/10/9  有最优尺寸？
//                Camera.Parameters parameters = mCamera.getParameters();
//                if (null != parameters) {
//                    parameters.setPreviewSize(640, 480);//设置尺寸
//                    mCamera.setParameters(parameters);
//                }
//            } catch (Exception e) {
//                DLLog.e("摄像头 MainActivity", "错误 主页面 分辨率" + " setupCamera-->" + e.toString());
//                Constant.RESTART_PHONE_OR_AUDIO = 1;
//                e.printStackTrace();
//            }
//        }
    }

    private void setPreviewFormat() {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                parameters.setPreviewFormat(ImageFormat.NV21);
                mCamera.setParameters(parameters);
                HttpApi.i("设置NV21完成");
            }
        } catch (Exception e) {
            HttpApi.i("设置NV21失败");
            e.printStackTrace();
        }

//        if (null != mCamera) {
//            try {
//                Camera.Parameters parameters1 = mCamera.getParameters();
//                if (null != parameters1) {
//                    parameters1.setPreviewFormat(ImageFormat.NV21);//指定图像的格式
//                    // (NV21：是一种YUV420SP格式，紧跟Y平面的是VU交替的平面)
//                    mCamera.setParameters(parameters1);
//                }
//            } catch (Exception e) {
//                DLLog.e("摄像头 MainActivity", "错误 主页面 图像格式" + " setupCamera-->" + e.toString());
//                Constant.RESTART_PHONE_OR_AUDIO = 1;
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * SurfaceView 有变化时被触发，一般情况这个SurfaceView 只用来获取数据不会触发
     *
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void setupChanged(int format, int width, int height) {
//        Log.e(TAG, "相机" + "setupChanged");
    }

    /**
     * 需要启动Preview的时机会被调用，返回false则在UI创建完之后立即启动,否则不启动预览，由使用者自己控制启动时机
     *
     * @return
     */
    @Override
    public boolean startPreviewLater() {
//        Log.e(TAG, "相机" + "startPreviewLater");
        return false;
    }

    /**
     * 启动Preview之后会被调用，返回相应的图像数据和时间戳，注意这里的时间戳不是底层驱动的时间戳
     *
     * @param data      输入的图像数据
     * @param width     图像宽度
     * @param height    图像宽度
     * @param format    图像格式
     * @param timestamp
     * @return
     */
    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
//        Log.e(TAG, "相机" + "onPreview");
        // TODO: 2018/10/13 在这里对data做判断，可以分辨出摄像头是否死亡或插线松动，如果这里不行就在心跳中查看摄像头的情况，要写本地日志
        //检测输入的图像中存在的人脸，输出结果和初始化时设置的参数有密切关系,检测到的人脸会add到此result
        AFT_FSDKError err = engine.AFT_FSDK_FaceFeatureDetect(data, width, height, AFT_FSDKEngine.CP_PAF_NV21, result);
        // TODO: 2018/10/13 这里最好加个error.getCode()的判断
//        Log.d(TAG, "AFT_FSDK_FaceFeatureDetect =" + err.getCode());
//        Log.d(TAG, "Face=" + result.size());
//        for (AFT_FSDKFace face : result) {
//            Log.d(TAG, "虹软:" + face.toString());
////            Rect(145, 164 - 385, 404),1
////            Rect(169, 166 - 429, 426),1
////            Rect(140, 164 - 404, 428),1
//        }

        if (mImageNV21 == null) {
//            Log.e(TAG, "人脸识别 " + "开始1");
            if (!result.isEmpty()) {
//                Log.e(TAG, "人脸识别 " + "开始2");
                mAFT_FSDKFace = result.get(0).clone();//保存集合中第一个人脸信息
                mImageNV21 = data.clone();//保存图像数据
            } else {
//                mHandler.postDelayed(hide, 3000);
            }
        }

        if (DeviceConfig.PRINTSCREEN_STATE == 2) {
            if (data != null) {
                picData = data.clone();//保存图像数据
            }
        }

        //保存人脸框数组
        Rect[] rects = new Rect[result.size()];
        for (int i = 0; i < result.size(); i++) {
            rects[i] = new Rect(result.get(i).getRect());
        }
        //清空人脸信息集合
        result.clear();
        //返回人脸框数据用于渲染
        return rects;
    }

    /**
     * 渲染图像数据传递到 GLRender 之前会触发，这里可以对图像数据再次修改，例如在上面画框之类的操作都可以
     *
     * @param data
     */
    @Override
    public void onBeforeRender(CameraFrameData data) {
//        Log.e(TAG, "相机" + "onBeforeRender");
    }

    /**
     * 渲染完成之后被触发，这里可以做一些资源释放之类的操作，也可以什么都不做
     *
     * @param data
     */
    @Override
    public void onAfterRender(CameraFrameData data) {
//        Log.e(TAG, "相机" + "onAfterRender");
//        if (null == data.getParams()) {
//            Log.e(TAG, "相机" + "getParams" + "为空");
//        }
        mGLSurfaceView.getGLES2Render().draw_rect((Rect[]) data.getParams(), Color.GREEN, 2);

        // TODO: 2018/10/11 这个方法用来在人脸框旁边加字(用于参考，https://www.jianshu.com/p/8dee89ec4a24)
//        canvas.drawText(userName, aftFsdkFace.getRect().right + 30, aftFsdkFace.getRect().top + 50, paint);
    }

    /**
     * 开始人脸录入
     */
    private void faceDetectInput() {
        startActivity(new Intent(this, PhotographActivity2.class));
        // TODO: 2018/6/19 sendMainMessager(MSG_FACE_DOWNLOAD, null);
    }

    @Override
    public void setData(ComBean comRecData) {
        Message message = Message.obtain();
        message.what = MSG_CARD_KEY_INCOM;
        message.obj = comRecData;
        handler.sendMessage(message);
    }

    class FRAbsLoop extends AbsLoop {

        AFR_FSDKVersion version = new AFR_FSDKVersion();
        AFR_FSDKEngine engine = new AFR_FSDKEngine();
        AFR_FSDKFace result = new AFR_FSDKFace();

        //拿到本地数据库脸信息表
//        List<FaceRegist> mResgist = ArcsoftManager.getInstance().mFaceDB.mRegister;
        List<FaceRegist> mResgist = ArcsoftManager.getInstance().mFaceDB.mRegister1;


        private final Object lock = new Object();
        private boolean pause = false;

        /**
         * 调用这个方法实现暂停线程
         */
        void pauseThread() {
            Log.e("人脸识别", " initFaceDetect--> 这里走了吗4");
            pause = true;
        }

        /**
         * 调用这个方法实现恢复线程的运行
         */
        void resumeThread() {
            Log.e("人脸识别", " initFaceDetect--> 这里走了吗3");
            pause = false;
            synchronized (lock) {
                lock.notifyAll();//唤醒所有正在等待该对象的线程
            }
        }

        /**
         * 注意：这个方法只能在run方法里调用，不然会阻塞主线程，导致页面无响应
         */
        void onPause() {
            Log.e("人脸识别", " initFaceDetect--> 这里走了吗2");
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    DLLog.e(TAG, "错误 FRAbsLoop onPause-> " + e.toString());
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void setup() {
            Log.e("人脸识别", " initFaceDetect--> 这里走了吗1");
            AFR_FSDKError error = engine.AFR_FSDK_InitialEngine(arc_appid, fr_key);
            //Log.v(FACE_TAG, "AFR_FSDK_InitialEngine = " + error.getCode());
            error = engine.AFR_FSDK_GetVersion(version);
            //Log.v(FACE_TAG, "FR=" + version.toString() + "," + error.getCode()); //(210, 178 -
            // 478, 446), degree = 1　780, 2208 - 1942, 3370
        }

        @Override
        public void loop() {
//            Log.e("人脸识别", " initFaceDetect--> 这里走了吗");
//            Log.v(FACE_TAG, "loop1:" + mImageNV21 + "/" + identification);
            while (pause) {
                onPause();
            }
//            try {
//                Thread.sleep(1 * 1000);//一秒一次
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            if (DeviceConfig.PRINTSCREEN_STATE == 2) {
                if (null != picData) {
                    //将byte数组转成bitmap再转成图片文件
                    byte[] data = picData.clone();
                    if (data != null && data.length > 0) {
                        Bitmap bmp = BitmapUtils.byteToFile(data, mWidth, mHeight);
                        File file = null;
                        if (null != bmp) {
                            file = BitmapUtils.saveBitmap(bmp);//本地截图文件地址
                        }
                        if (null != file && !TextUtils.isEmpty(file.getPath())) {
                            uploadToQiNiu(file, 1);//这里做上传到七牛的操作，不返回图片URL
                        } else {
                            faceOpenUrl = "";
                        }
                        DeviceConfig.PRINTSCREEN_STATE = 0;//图片处理完成,重置状态
                        sendMainMessager(MSG_CARD_OPENLOCK, faceOpenUrl);
                        file = null;
                        bmp = null;
                        data = null;
                    }
                    picData = null;
                }
            }

            if (DeviceConfig.PRINTSCREEN_STATE == 0) {//开启截图、上传图片、开门、上传日志流程
                if (mImageNV21 != null && identification) {//摄像头检测到人脸信息且处于人脸识别状态
//                    DLLog.e("人脸识别", "开始");
//                        long time = System.currentTimeMillis();
                    //检测输入图像中的人脸特征信息，输出结果保存在 AFR_FSDKFace feature
                    //data 输入的图像数据,width 图像宽度,height 图像高度,format 图像格式,face 已检测到的脸框,ori 已检测到的脸角度,
                    // feature 检测到的人脸特征信息
                    AFR_FSDKError error = engine.AFR_FSDK_ExtractFRFeature(mImageNV21, mWidth, mHeight,
                            AFR_FSDKEngine.CP_PAF_NV21, mAFT_FSDKFace.getRect(), mAFT_FSDKFace.getDegree(), result);
//                        Log.d(TAG, "AFR_FSDK_ExtractFRFeature cost :" + (System.currentTimeMillis() - time) + "ms");
//                        Log.d(TAG, "Face=" + result.getFeatureData()[0] + "," + result.getFeatureData()[1] + "," +
//                                result.getFeatureData()[2] + "," + error.getCode());
                    if (error.getCode() != 0 && error.getCode() != 3) {
                        DLLog.e(TAG, "人脸信息提取error " + error.getCode());
                        //如果mImageNV21置为null，就要加return，否则可能在后续流程mImageNV21.clone()中报空指针
//                        mImageNV21 = null;
//                        return;
                    }

                    AFR_FSDKMatching score = new AFR_FSDKMatching();//这个类用来保存特征信息匹配度
                    float max = 0.0f;//匹配度的值
                    String name = null;

                    //遍历本地信息表
                    for (FaceRegist fr : mResgist) {
//                        Log.v("人脸识别", "loop:" + mResgist.size() + "/" + fr.mFaceList.size());
                        if (fr.mName.length() > 11) {
                            continue;
                        }
                        for (AFR_FSDKFace face : fr.mFaceList) {
                            //比较两份人脸特征信息的匹配度(result 脸部特征信息对象,face 脸部特征信息对象,score 匹配度对象)
//                            Log.e("人脸识别 比较值 ", "result " + result.toString() + " face " + face.toString());
                            error = engine.AFR_FSDK_FacePairMatching(result, face, score);
//                            Log.d("人脸识别", "Score:" + score.getScore() + " error " + error.getCode());
                            if (max < score.getScore()) {
                                max = score.getScore();//匹配度赋值
                                name = fr.mName;
                                if (max > Constant.FACE_MAX) {//匹配度的值高于设定值,退出循环
//                                    DLLog.e("人脸识别", "匹配度的值高于设定值 " + max);
                                    break;
                                }
                            }
                        }
                    }

//                    Log.v("人脸识别", "fit Score:" + max + ", NAME:" + name);
                    if (max > Constant.FACE_MAX) {//匹配度的值高于设定值,发出消息,开门
                        if (null != name && !cardRecord.checkLastCardNew(name)) {//判断距离上次刷脸时间是否超过10秒
                            //fr success.
                            //Log.v(FACE_TAG, "置信度：" + (float) ((int) (max * 1000)) / 1000.0);
                            //将byte数组转成bitmap再转成图片文件
                            if (DeviceConfig.PRINTSCREEN_STATE == 0) {
                                DeviceConfig.PRINTSCREEN_STATE = 1;//开启截图、上传图片、开门、上传日志流程
                                byte[] data = mImageNV21.clone();
                                if (data != null && data.length > 0) {
                                    Bitmap bmp = BitmapUtils.byteToFile(data, mWidth, mHeight);
                                    File file = null;
                                    if (null != bmp) {
                                        file = BitmapUtils.saveBitmap(bmp);//本地截图文件地址
                                    }
                                    String[] parameters = new String[2];
                                    parameters[0] = name;
                                    if (null != file && !TextUtils.isEmpty(file.getPath())) {
                                        uploadToQiNiu(file, 3);//这里做上传到七牛的操作，不返回图片URL
                                        parameters[1] = faceOpenUrl;
                                    } else {
                                        parameters[1] = "";
                                    }
//                                    DLLog.e("人脸识别", "发出消息 " + name);
                                    DeviceConfig.PRINTSCREEN_STATE = 0;//人脸开门图片处理完成（异步处理）,重置状态
                                    sendMainMessager(MSG_FACE_OPENLOCK, parameters);
                                    file = null;
                                    bmp = null;
                                    data = null;
                                    name = null;
                                } else {
                                    DeviceConfig.PRINTSCREEN_STATE = 0;//重置状态
                                }
                            }
                        }
                    }
//                    result = null;
//                    result = new AFR_FSDKFace();
                    mAFT_FSDKFace = null;
                    mImageNV21 = null;
                }
            }
        }

        @Override
        public void over() {
            AFR_FSDKError error = engine.AFR_FSDK_UninitialEngine();//销毁引擎，释放内存资源
            //Log.v(FACE_TAG, "AFR_FSDK_UninitialEngine : " + error.getCode());
        }
    }

    private void commitData() {
//        OkHttpUtils
//                .postFile()
//                .url(url)
//                .file(file)
//                .build()
//                .execute(new Callback() {
//                    @Override
//                    public Object parseNetworkResponse(Response response, int id) throws Exception {
//                        return null;
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Object response, int id) {
//
//                    }
//                });
    }

    private void uploadBin() {
        /*if (true) {//匹配，开门
            byte[] data = mImageNV21;
            String name = "";
            if (data != null && data.length > 0) {
                Bitmap bmp = BitmapUtils.byteToFile(data, mWidth, mHeight);
                File file = null;
                if (null != bmp) {
                    file = BitmapUtils.saveBitmap(bmp);//本地截图文件地址
                }
                String[] parameters = new String[2];
                parameters[0] = name;
                if (null != file && !TextUtils.isEmpty(file.getPath())) {
                    uploadToQiNiu(file, 3);//这里做上传到七牛的操作，不返回图片URL
                    parameters[1] = faceOpenUrl;
                } else {
                    parameters[1] = "";
                }
                DLLog.e("人脸识别", "发出消息 " + name);
                sendMainMessager(MSG_FACE_OPENLOCK, parameters);
                // TODO: 2018/7/25 匹配成功后删除tempface文件夹中所有的文件
                file = null;
                bmp = null;
                data = null;
                name = null;
            }
        } else {
            DeviceConfig.PRINTSCREEN_STATE = 0;//重置状态
        }
        mAFT_FSDKFace = null;
        mImageNV21 = null;*/
    }

    /**
     * 上传图片至七牛
     *
     * @param file 本地图片
     * @param i    开门方式:1卡2手机3人脸4邀请码5离线密码6临时密码
     * @return
     */
    private void uploadToQiNiu(final File file, final int i) {
        //创建地址
        faceOpenUrl = "door/img/" + System.currentTimeMillis() + ".jpg";
        final ImgFile imgFile = new ImgFile();
        imgFile.setImg_localurl(file.getAbsolutePath());
        imgFile.setImg_uploadurl(faceOpenUrl);
        OkHttpUtils.post().url(API.QINIU_IMG).build().execute(new StringCallback() {//七牛token值不固定，每次请求使用
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.i(TAG, "获取七牛token失败 e" + e.toString());
                DbUtils.getInstans().insertOneImg(imgFile);//获取token失败，图片存在本地
            }

            @Override
            public void onResponse(final String response, int id) {
                new Thread() {
                    @Override
                    public void run() {
                        String token = JsonUtil.getFieldValue(response, "data");
                        Log.i(TAG, "获取七牛token成功 开始上传照片  token" + token);
                        Log.e(TAG, "file七牛储存地址：" + faceOpenUrl);
                        Log.e(TAG, "file本地地址：" + file.getPath() + "file大小" + file.length());
                        uploadManager.put(file.getPath(), faceOpenUrl, token, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                if (info.isOK()) {
                                    Log.e(TAG, "七牛上传图片成功");
                                    try {//删除本地图片文件
                                        if (file != null) {
                                            file.delete();
                                        }
                                    } catch (Exception e) {
                                        DLLog.e(TAG, "错误 uploadToQiNiu error-> " + e.toString());
                                    }
                                } else {
                                    Log.e(TAG, "七牛上传图片失败");
                                    DbUtils.getInstans().insertOneImg(imgFile);//获取token失败，图片存在本地
                                }
                                Log.e("七牛", "七牛info" + info.toString());
                            }
                        }, null);
                    }
                }.start();
            }
        });
    }

    /**
     * 上传本地日志到七牛
     *
     * @param file
     */
    private void uploadLogToQiNiu(final File file) {
        final String url = "door/log/" + MacUtils.getMac() + "_" + System.currentTimeMillis() + ".txt";
        OkHttpUtils.post().url(API.QINIU_IMG).build().execute(new StringCallback() {//七牛token值不固定，每次请求使用
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.i(TAG, "获取七牛token失败 e" + e.toString());
            }

            @Override
            public void onResponse(final String response, int id) {
                new Thread() {
                    @Override
                    public void run() {
                        String token = JsonUtil.getFieldValue(response, "data");
//                        Log.e(TAG, "获取七牛token成功 开始上传照片  token" + token + " file七牛储存地址：" + url + " file本地地址：" + file
//                                .getPath() + "file大小" + file.length());
                        uploadManager.put(file.getPath(), url, token, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                if (info.isOK()) {
                                    Log.e(TAG, "七牛上传日志成功");
                                } else {
                                    Log.e(TAG, "七牛上传日志失败");
                                }
                                Log.e(TAG, "七牛info" + info.toString());
                            }
                        }, null);
                    }
                }.start();
            }
        });
    }

    /****************************虹软相关end*********************************************/

    /****************************生命周期start*******************************************/
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "MainActivity/onStart-->");
        if (isRestartPlay) {
            isRestartPlay = false;
            advertiseHandler.start(adverErrorCallBack);
        }

        if (!as.isScrolled()) {
            as.setScrolled(true);
            as.autoScroll(isTongGaoFrist);
        }
    }

    private boolean isRestartPlay = false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "MainActivity/onResume-->");
        if (faceHandler != null) {
            Log.e(TAG, "人脸95");
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_CONTRAST, 4000);
//            faceHandler.sendEmptyMessageDelayed(MSG_ID_CARD_DETECT_RESTART, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "MainActivity/onPause-->");
        if (null != advertiseHandler) {
            advertiseHandler.pause(adverErrorCallBack);
        }
        isRestartPlay = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "MainActivity/onStop-->");
        if (faceHandler != null) {
            faceHandler.sendEmptyMessageDelayed(MSG_FACE_DETECT_PAUSE, 1000);
//            faceHandler.sendEmptyMessageDelayed(MSG_ID_CARD_DETECT_PAUSE, 1000);
        }

        if (as.isScrolled()) {
            as.setScrolled(false);
            as.change();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "MainActivity/onRestart-->");
        setCurrentStatus(CALL_MODE);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
//        MainApplication.getRefWatcher(this).watch(this);
        unbindService(serviceConnection);
//        unregisterReceiver(receive);
        CloseComPort(control);//关闭串口

//        disableReaderMode();//注释：永远读卡
        if (netTimer != null) {
            netTimer.cancel();
            netTimer = null;
        }

        if (null != weituoDialog) {
            weituoDialog.dismiss();
        }

//        mSurfaceView.setVisibility(View.GONE);
//        mGLSurfaceView.setVisibility(View.GONE);

        try {
            identification = false;
            if (null != mFRAbsLoop) {
                mFRAbsLoop.shutdown();
            }
            AFT_FSDKError err = engine.AFT_FSDK_UninitialFaceEngine();
        } catch (Exception e) {
            DLLog.e("设备重启 ", "MainActivity-->onDestroy " + e.toString());
            onReStartVideo();
            e.printStackTrace();
        }

        if (as != null) {
            as.removeAll();
        }

        if (faceHandler != null) {
            faceHandler.removeCallbacksAndMessages(null);
        }

        OkHttpUtils.getInstance().cancelTag(MainService.class);//取消网络请求


        super.onDestroy();
    }

    private void onReStartVideo() {
//        if (Constant.RESTART_AUDIO) {
//            DLLog.e("wh", "进行流媒体的重启");
//            startActivity(new Intent(this, PhotographActivity.class));
//        } else {
//            Intent intent1 = new Intent(Intent.ACTION_REBOOT);
//            intent1.putExtra("nowait", 1);
//            intent1.putExtra("interval", 1);
//            intent1.putExtra("window", 0);
//            sendBroadcast(intent1);
//        }
        Intent intent1 = new Intent(Intent.ACTION_REBOOT);
        intent1.putExtra("nowait", 1);
        intent1.putExtra("interval", 1);
        intent1.putExtra("window", 0);
        sendBroadcast(intent1);
    }

    /****************************生命周期end*********************************************/

    /**
     * 关闭软键盘
     */
    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void displayBriefMemory() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        Log.i(TAG, "系统剩余内存:" + (info.availMem >> 10) / 1024 + "M");
        Log.i(TAG, "系统是否处于低内存运行：" + info.lowMemory);
        Log.i(TAG, "当系统剩余内存低于" + (info.threshold) / 1024 + "时就看成低内存运行");

        ActivityManager activityManager1 = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = activityManager1.getMemoryClass();
        Log.e(TAG, "内存 " + memoryClass);
        int largeMemoryClass = activityManager1.getLargeMemoryClass();
        Log.e(TAG, "最大内存 " + largeMemoryClass);

        float maxMemory = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
        //当前分配的总内存
        float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
        //剩余内存
        float freeMemory = (float) (Runtime.getRuntime().freeMemory() * 1.0 / (1024 * 1024));
        Log.e(TAG, "分配内存" + maxMemory + " " + freeMemory + " " + totalMemory);
    }
}
