package android_serialport_api.utils;

/**
 * Created by William on 2018/9/6.
 */

public class SysOptionCode {
    public static class System {
        public static final int DEFAULT_HEX = 0x0;// 默认值

    }

    /**
     * 数据帧数据
     */
    public static class FrameData {
        public static final int FRAMEDATE_CHONGQI= 99;
        public static final int FRAMEDATE_OPENDENG = 89;
        public static final int FRAMEDATE_CLOSEDENG = 96;
        public static final int SEND_XINTIAO = 88;
        public static final int OPEN_XINTIAO = 97;
        public static final int CLOSE_XINTIAO = 98;

        public static final int FRAMEDATE_HEADER_1 = 0x02;
        public static final int FRAMEDATE_HEADER_2 = 0x50;
        public static final int FRAMEDATE_HEADER_3 = 0x01;
        public static final int FRAMEDATE_HEADER_4 = 0x02;
        public static final int FRAMEDATE_HEADER_5 = 0x00;
        public static final int FRAMEDATE_HEADER_6 = 0x04;
        public static final int FRAMEDATE_HEADER_7 = 0x01;
        public static final int FRAMEDATE_HEADER_8 = 0x03;
        // 版本
        public static final int FRAMEDATE_VERSIONS_1 = 0xF5;
        // 命令字 21刷卡 22 更新的钥匙到刷卡版
        public static final int FRAMEDATE_COMMAND_WORD = 0x21;
        // 空卡号
        // public static final int FRAMEDATE_KONG_CARD = 0x00;
        // 1、卡开锁 2、手机开锁 3、门口外面按键 4、门口里面开锁 5、远程 wifi 开门 6、人脸识别开门 7、指纹开门
        public static final int FRAMEDATE_KAISUO_LEIBIE = 0x02;

        public static final int FRAMEDATE_END_1 = 0XAC;
        public static final int FRAMEDATE_END_2 = 0XAA;
        public static final int FRAMEDATE_END_3 = 0XCA;
        public static final int FRAMEDATE_END_4 = 0XCC;
    }

    public static class Allclose {
        public static final int ALLCOLSE1 = 0X01;
        public static final int ALLCOLSE2 = 0X01;
        public static final int ALLCOLSE3 = 0X01;
        public static final int ALLCOLSE4 = 0X00;
        public static final int ALLCOLSE5 = 0XB2;
    }

    /**
     * 状态码指令
     */
    public static class StatusCode {
        public static final int STATUSCODE_INIT = 0XB0;// 整机初始化
        public static final int STATUSCODE_BOOT = 0XB1;// 开机状态码
        public static final int STATUSCODE_SHUTDOWN = 0XB2;// 关机状态码
        public static final int STATUSCODE_COLSEALL = 0XB3;// 全部关闭
        public static final int STATUSCODE_DEL_ELE = 0XB4;// 删除电极
        public static final int STATUSCODE_TIME_CLEAR_ZERO = 0XB5;// 时间倒计时为零
    }
}
