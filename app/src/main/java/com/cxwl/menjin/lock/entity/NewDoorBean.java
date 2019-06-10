package com.cxwl.menjin.lock.entity;

/**
 * Created by William on 2018/6/3.
 */

public class NewDoorBean {

    /**
     * xdoorBh : xd20190508162339
     * gongsiBh : gs000018
     * xiangmuBh : xm00000029
     * qiqu : 二期
     * loudong : 1栋
     * danyuan : 3单元
     * mac : 12:12:12:12:12:11
     * key : null
     * name : 胖胖单元门
     * type : 1
     * shanchu : 1
     * gongsiName : null
     * version : null
     * lixianMima : null
     * online : false
     * versionId : 19
     * voice : 50
     * luminance : 50
     * xiangmuName : 门禁项目
     */

    private String xdoorBh;
    private String gongsiBh;
    private String xiangmuBh;
    private String qiqu;
    private String loudong;
    private String danyuan;
    private String mac;
    private Object key;
    private String name;
    private int type;//门禁类型:0大门,1单元门,2区期门
    private int shanchu;
    private Object gongsiName;
    private Object version;
    private Object lixianMima;
    private boolean online;
    private int versionId;
    private int voice;//声音
    private int luminance;//亮度
    private String xiangmuName;

    public String getXdoorBh() {
        return xdoorBh;
    }

    public void setXdoorBh(String xdoorBh) {
        this.xdoorBh = xdoorBh;
    }

    public String getGongsiBh() {
        return gongsiBh;
    }

    public void setGongsiBh(String gongsiBh) {
        this.gongsiBh = gongsiBh;
    }

    public String getXiangmuBh() {
        return xiangmuBh;
    }

    public void setXiangmuBh(String xiangmuBh) {
        this.xiangmuBh = xiangmuBh;
    }

    public String getQiqu() {
        return qiqu;
    }

    public void setQiqu(String qiqu) {
        this.qiqu = qiqu;
    }

    public String getLoudong() {
        return loudong;
    }

    public void setLoudong(String loudong) {
        this.loudong = loudong;
    }

    public String getDanyuan() {
        return danyuan;
    }

    public void setDanyuan(String danyuan) {
        this.danyuan = danyuan;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getShanchu() {
        return shanchu;
    }

    public void setShanchu(int shanchu) {
        this.shanchu = shanchu;
    }

    public Object getGongsiName() {
        return gongsiName;
    }

    public void setGongsiName(Object gongsiName) {
        this.gongsiName = gongsiName;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public Object getLixianMima() {
        return lixianMima;
    }

    public void setLixianMima(Object lixianMima) {
        this.lixianMima = lixianMima;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    public int getLuminance() {
        return luminance;
    }

    public void setLuminance(int luminance) {
        this.luminance = luminance;
    }

    public String getXiangmuName() {
        return xiangmuName;
    }

    public void setXiangmuName(String xiangmuName) {
        this.xiangmuName = xiangmuName;
    }

    @Override
    public String toString() {
        return "NewDoorBean{" +
                "xdoorBh='" + xdoorBh + '\'' +
                ", gongsiBh='" + gongsiBh + '\'' +
                ", xiangmuBh='" + xiangmuBh + '\'' +
                ", qiqu='" + qiqu + '\'' +
                ", loudong='" + loudong + '\'' +
                ", danyuan='" + danyuan + '\'' +
                ", mac='" + mac + '\'' +
                ", key=" + key +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", shanchu=" + shanchu +
                ", gongsiName=" + gongsiName +
                ", version=" + version +
                ", lixianMima=" + lixianMima +
                ", online=" + online +
                ", versionId=" + versionId +
                ", voice=" + voice +
                ", luminance=" + luminance +
                ", xiangmuName='" + xiangmuName + '\'' +
                '}';
    }
}
