package android_serialport_api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.utils.MyFunc;
import android_serialport_api.utils.SysOptionCode;

/**
 * Created by William on 2018/9/6.
 */

public class SerialHelper implements Serializable {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    //    private String sPort = "/dev/s3c2410_serial0";
//    private String sPort = "/dev/ttyS4";//门口机才能用ttyS4
    private String sPort = "/dev/ttyS1";
    private int iBaudRate = 9600;
    private boolean _isOpen = false;
    private byte[] _bLoopData = new byte[]{0x30};
    private int iDelay = 500;
    private onDataReceived dataReceived;

    // ----------------------------------------------------
    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }

    public void setDataReceived(onDataReceived dataReceived) {
        this.dataReceived = dataReceived;
    }

    public SerialHelper() {
        // this("/dev/s3c2410_serial0", 9600);
    }

    public SerialHelper(String sPort) {
        this(sPort, 9600);
    }

    public SerialHelper(String sPort, String sBaudRate) {
        this(sPort, Integer.parseInt(sBaudRate));
    }

    // ----------------------------------------------------
    public void open() throws SecurityException, IOException, InvalidParameterException {
        _isOpen = true;
        mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();
    }

    // ----------------------------------------------------
    public void close() {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        _isOpen = false;
    }

    // 开门
    public void openDoor() {
        byte codeByte[] = phoneOpenDoor("00000000");
        send(codeByte);
    }

    // 开灯
    public void openDeng() {
        byte codeByte[] = deng(SysOptionCode.FrameData.FRAMEDATE_OPENDENG);
        send(codeByte);
    }

    // 重启
    public void chongqi() {
        byte codeByte[] = deng(SysOptionCode.FrameData.FRAMEDATE_CHONGQI);
        send(codeByte);
    }

    // 关灯
    public void closeDeng() {
        byte codeByte[] = deng(SysOptionCode.FrameData.FRAMEDATE_CLOSEDENG);
        send(codeByte);
    }

    // 发送心跳包
    public void sendXintiao() {
        byte codeByte[] = deng(SysOptionCode.FrameData.SEND_XINTIAO);
        send(codeByte);
    }

    // 打开心跳
    public void openXintiao() {
        byte codeByte[] = deng(SysOptionCode.FrameData.OPEN_XINTIAO);
        send(codeByte);
    }

    // 关闭心跳
    public void closeXintiao() {
        byte codeByte[] = deng(SysOptionCode.FrameData.CLOSE_XINTIAO);
        send(codeByte);
    }

    // 设置继电器时间
    public void setRelayTime(int time) {
        byte codeByte[] = setRelayTime("00000000", time);
        send(codeByte);
    }

    public int setCurrentLedStatu(int value) {
        File ChannelInfoFile = new File("/sys/ledlight/ledlight");
        File ChannelInfoFile1 = new File("/sys/ledlight/ledlight1");
        String status = WriteStringToFile(ChannelInfoFile, String.valueOf(value)); // From
        String status1 = WriteStringToFile(ChannelInfoFile1, String.valueOf(value)); // From
        // driver
        if (status == null && status1 == null)
            return 0;
        return 1;
    }

    public String WriteStringToFile(File file, String str) {
        if (!file.exists()) {
            return null;
        }
        OutputStream output = null;
        OutputStreamWriter outputWrite = null;
        PrintWriter print = null;

        try {
            // SystemProperties.set("sys.hdmi_screen.scale",String.valueOf(value));
            output = new FileOutputStream(file);
            outputWrite = new OutputStreamWriter(output);
            print = new PrintWriter(outputWrite);

            print.print(str);
            print.flush();
            output.close();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ----------------------------------------------------
    public void send(byte[] bOutArray) {
        try {
            mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    public void sendHex(String sHex) {
        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }

    // ----------------------------------------------------
    public void sendTxt(String sTxt) {
        byte[] bOutArray = sTxt.getBytes();
        send(bOutArray);
    }

    // StringBuffer bufferStr = new StringBuffer();
    List<Byte> bytes = new ArrayList<Byte>();

    // ----------------------------------------------------
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (_isOpen) {
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[512];
                    int size = mInputStream.read(buffer);
                    if (size > 0) {
                        ComBean ComRecData = new ComBean(sPort);
                        for (int i = 0; i < size; i++) {
                            bytes.add(buffer[i]);
                        }
//                        Log.i("返回值", buffer[0] + "," + buffer[1] + "," + buffer[2] + "," + buffer[3] + "," + buffer[4]
//                                + "," + buffer[5] + "," + buffer[6] + "," + buffer[7]+" sPort"+sPort);
//                        System.out.println("数据长度为" + bytes.size());
                        if (bytes.size() == 8) {
                            Byte versions = bytes.get(1);
//                            System.out.println("versions值为" + versions);
//                            System.out.println("总值为" + bytes.get(0) + "," + bytes.get(1) + "," + bytes.get(2) + ","
//                                    + bytes.get(3) + "," + bytes.get(4) + "," + bytes.get(5) + "," + bytes.get(6) +
// ","
//                                    + bytes.get(7) + "");
                            switch (versions) {
                                case 02:// 刷卡
                                    byte[] cardBtyes = new byte[4];
//                                    byte[] cardBtyes = new byte[5];
                                    // 遍历卡号
                                    for (int i = 0; i < cardBtyes.length; i++) {
//                                        Log.e("返回值", "打印byte " + bytes.get(i + 2));
                                        cardBtyes[i] = bytes.get(i + 2);
                                    }
                                    String cardStr = ByteArrToHex(cardBtyes);
//                                    String cardStr = byte2HexString(cardBtyes);
                                    ComRecData.sCard = cardStr;
                                    ComRecData.index = 1;
//                                    Log.i("kaimen", "kaimenchenggong" + bytes.toString());
                                    if (null != dataReceived) {
                                        dataReceived.setData(ComRecData);
                                    }
                                    bytes.clear();
                                    break;
                                case 01:// 按键
//                                    System.out.println("进来了3");
                                    byte[] keyBtyes = new byte[1];
                                    keyBtyes[0] = bytes.get(6);
                                    String keyStr = ByteArrToHex(keyBtyes);
                                    ComRecData.sKey = keyStr;
                                    ComRecData.index = 2;
                                    if (null != dataReceived) {
                                        dataReceived.setData(ComRecData);
                                    }
                                    bytes.clear();
                                    break;
                                case 03: // 人体感应
                                case 00: // 人体感应
//                                    System.out.println("进来了人体感应");
                                    bytes.clear();
                                    ComRecData.index = 5;
                                    if (null != dataReceived) {
//                                        dataReceived.setData(ComRecData);
                                        closeDeng();
                                    }
                                    sendXintiao();
                                    break;
                                case 8:// 心跳
                                    bytes.clear();
                                    sendXintiao();
                                    break;
                                case 81:// 心跳打开
                                    bytes.clear();
                                    break;
                                case 82:// 心跳关闭
                                    bytes.clear();
                                    break;
                                default:
                                    bytes.clear();
                                    break;
                            }
                        }
                    }
                    try {
                        Thread.sleep(50);// 延时50ms
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bytes.size() > 16) {
                        bytes.clear();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * byte[]转换成字符串
     *
     * @param b
     * @return
     */
    public static String byte2HexString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        int length = b.length;
        for (int i = 0; i < b.length; i++) {
            String stmp = Integer.toHexString(b[i] & 0xff);
            if (stmp.length() == 1) sb.append("0" + stmp);
            else sb.append(stmp);
        }
        return sb.toString();
    }

    public static String ByteArrToHex(byte[] inBytArr)// 字节数组转转hex字符串
    {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
            strBuilder.append("");
        }
        return strBuilder.toString();
    }

    public static String Byte2Hex(Byte inByte)// 1字节转2个Hex字符
    {
        return String.format("%02x", inByte).toUpperCase();
    }

    public static void main(String[] args) {
        System.out.print(Byte2Hex((byte) 47));
    }

    public byte[] recByte(byte[] buffer, int size) {
        byte[] bRec = new byte[size];
        for (int i = 0; i < size; i++) {
            bRec[i] = buffer[i];
        }
        return bRec;
    }

    // ----------------------------------------------------
    private class SendThread extends Thread {
        public boolean suspendFlag = true;// 控制线程的执行

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                send(getbLoopData());
                try {
                    Thread.sleep(iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        // 唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    // ----------------------------------------------------
    public int getBaudRate() {
        return iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (_isOpen) {
            return false;
        } else {
            iBaudRate = iBaud;
            return true;
        }
    }

    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    // ----------------------------------------------------
    public String getPort() {
        return sPort;
    }

    public boolean setPort(String sPort) {
        if (_isOpen) {
            return false;
        } else {
            this.sPort = sPort;
            return true;
        }
    }

    // ----------------------------------------------------
    public boolean isOpen() {
        return _isOpen;
    }

    // ----------------------------------------------------
    public byte[] getbLoopData() {
        return _bLoopData;
    }

    // ----------------------------------------------------
    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }

    // ----------------------------------------------------
    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    // ----------------------------------------------------
    public void setHexLoopData(String sHex) {
        this._bLoopData = MyFunc.HexToByteArr(sHex);
    }

    // ----------------------------------------------------
    public int getiDelay() {
        return iDelay;
    }

    // ----------------------------------------------------
    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    // ----------------------------------------------------
    public void startSend() {
        if (mSendThread != null) {
            mSendThread.setResume();
        }
    }

    // ----------------------------------------------------
    public void stopSend() {
        if (mSendThread != null) {
            mSendThread.setSuspendFlag();
        }
    }

    public interface onDataReceived {
        public void setData(ComBean ComRecData);
    }

    /**
     * 开灯关灯
     *
     * @return byte数组
     */
    public static byte[] deng(int c) {
        byte codeByte[] = new byte[8];
        codeByte[0] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_1;
        codeByte[1] = (byte) c;
        codeByte[2] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_3;
        codeByte[3] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_4;
        codeByte[4] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_5;
        codeByte[5] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_6;
        codeByte[6] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_7;
        codeByte[7] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_8;
        bytesToHexStrings(codeByte);
        return codeByte;
    }

    /**
     * 手机开门
     *
     * @return byte数组
     */
    public static byte[] phoneOpenDoor(String card) {
        byte codeByte[] = new byte[8];

        codeByte[0] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_1;
        codeByte[1] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_2;
        codeByte[2] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_3;
        codeByte[3] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_4;
        codeByte[4] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_5;
        codeByte[5] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_6;
        codeByte[6] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_7;
        codeByte[7] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_8;
        bytesToHexStrings(codeByte);
        return codeByte;
    }

    /**
     * 设置继电器时间
     *
     * @return byte数组
     */
    public static byte[] setRelayTime(String card, int s) {
        byte codeByte[] = new byte[16];
        codeByte[0] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_1;
        codeByte[1] = (byte) SysOptionCode.FrameData.FRAMEDATE_HEADER_2;
        codeByte[3] = (byte) SysOptionCode.FrameData.FRAMEDATE_VERSIONS_1;
        // 命令符 21开门
        codeByte[4] = (byte) 23;
        // 空卡号
        byte[] cards = card.getBytes();
        // 校验位数据
        byte[] pucBuff = new byte[9];
        for (int i = 0; i < cards.length; i++) {
            codeByte[5 + i] = cards[i];
            pucBuff[i] = cards[i];
        }
        // 继电器设置时间
        codeByte[13] = (byte) s;
        pucBuff[8] = codeByte[13];
        // 校验位
        byte[] jiaoyan = GetCrc16Ccitt((byte) 0, pucBuff, pucBuff.length);
        codeByte[14] = jiaoyan[0];
        codeByte[15] = jiaoyan[1];

        codeByte[2] = (byte) 13;
        bytesToHexStrings(codeByte);
        return codeByte;
    }

    // CRC校验
    public static byte[] GetCrc16Ccitt(byte wBase, byte[] pucBuff, int dwLen) {
        byte wRet = wBase;
        int dwCnt = 0;
        for (; dwCnt < dwLen; dwCnt++) {
            Crc16CcittUpdate(pucBuff[dwCnt], wRet);
        }
        return int2bytes(wRet);
    }

    public static void Crc16CcittUpdate(byte ucData, byte pwCrc) {
        byte x = pwCrc;
        x = (byte) ((byte) (x >> 8) | (x << 8));
        x ^= (byte) ucData;
        x ^= (byte) (x & 0xFF) >> 4;
        x ^= (x << 8) << 4;
        x ^= ((x & 0xFF) << 4) << 1;
        pwCrc = x;
    }

    // 高位在前，低位在后
    public static byte[] int2bytes(int num) {
        byte[] result = new byte[2];
        result[0] = (byte) ((num >>> 8) & 0xff);
        result[1] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    /**
     * byte数组转换成16进制字符数组
     *
     * @param src
     * @return
     */
    public static String[] bytesToHexStrings(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        String[] str = new String[src.length];

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                str[i] = "0";
            }
            str[i] = hv;
        }
//        Log.i("当前码值为", str + "");
        return str;
    }
}
