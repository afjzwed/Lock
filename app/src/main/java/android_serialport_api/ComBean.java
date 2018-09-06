package android_serialport_api;

/**
 * Created by William on 2018/9/6.
 */

public class ComBean {
    // 串口
    public String sComPort = "";
    // 刷卡的卡号
    public String sCard = "";
    // 按键值
    public String sKey = "";
    // 索引 1刷卡 2、按键 3、开门成功 4、设置继电器时间成功
    public int index = 0;

    // public String sCard = "";
    public ComBean(String sComPort) {
        super();
        this.sComPort = sComPort;
    }
}
