package com.cxwl.menjin.lock.entity;

/**
 * Created by William on 2018/5/17.
 */

public class FaceUrlBean {

    /**
     * lianYezhuid : null
     * xdoorBh : xd20190508162339
     * chiyouren : 秀熬汤
     * xdoorLianId : 17
     * yezhuId : 0
     * gongsiBh : gs000018
     * xiangmuBh : xm00000029
     * yezhuPhone : 15116852112
     * shanchu : 1
     * bangdingTime : 2019-05-08 16:54:35
     * xiangmuName : null
     * gongsiName : null
     * lianName : 秀熬汤
     * lianBinUrl : http://img0.hnchxwl.com/upload/lian/1557305670826.bin
     * lianUrl : https://wsq.oss-cn-shenzhen.aliyuncs.com/GG/GG_21060143.jpg
     * xdoorName : null
     * bindingName : null
     * bindingType : null
     * bindingId : null
     */

    private Object lianYezhuid;
    private String xdoorBh;
    private String chiyouren;//持有人
    private int xdoorLianId;
    private int yezhuId;//业主ID
    private String gongsiBh;
    private String xiangmuBh;
    private String yezhuPhone;//电话
    private int shanchu;//1新增-1删除2修改
    private String bangdingTime;
    private Object xiangmuName;
    private Object gongsiName;
    private String lianName;
    private String lianBinUrl;//脸地址
    private String lianUrl;
    private Object xdoorName;
    private Object bindingName;
    private Object bindingType;
    private Object bindingId;
    private String fileName;//文件名
    private String path;//本地路径

    public Object getLianYezhuid() {
        return lianYezhuid;
    }

    public void setLianYezhuid(Object lianYezhuid) {
        this.lianYezhuid = lianYezhuid;
    }

    public String getXdoorBh() {
        return xdoorBh;
    }

    public void setXdoorBh(String xdoorBh) {
        this.xdoorBh = xdoorBh;
    }

    public String getChiyouren() {
        return chiyouren;
    }

    public void setChiyouren(String chiyouren) {
        this.chiyouren = chiyouren;
    }

    public int getXdoorLianId() {
        return xdoorLianId;
    }

    public void setXdoorLianId(int xdoorLianId) {
        this.xdoorLianId = xdoorLianId;
    }

    public int getYezhuId() {
        return yezhuId;
    }

    public void setYezhuId(int yezhuId) {
        this.yezhuId = yezhuId;
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

    public String getYezhuPhone() {
        return yezhuPhone;
    }

    public void setYezhuPhone(String yezhuPhone) {
        this.yezhuPhone = yezhuPhone;
    }

    public int getShanchu() {
        return shanchu;
    }

    public void setShanchu(int shanchu) {
        this.shanchu = shanchu;
    }

    public String getBangdingTime() {
        return bangdingTime;
    }

    public void setBangdingTime(String bangdingTime) {
        this.bangdingTime = bangdingTime;
    }

    public Object getXiangmuName() {
        return xiangmuName;
    }

    public void setXiangmuName(Object xiangmuName) {
        this.xiangmuName = xiangmuName;
    }

    public Object getGongsiName() {
        return gongsiName;
    }

    public void setGongsiName(Object gongsiName) {
        this.gongsiName = gongsiName;
    }

    public String getLianName() {
        return lianName;
    }

    public void setLianName(String lianName) {
        this.lianName = lianName;
    }

    public String getLianBinUrl() {
        return lianBinUrl;
    }

    public void setLianBinUrl(String lianBinUrl) {
        this.lianBinUrl = lianBinUrl;
    }

    public String getLianUrl() {
        return lianUrl;
    }

    public void setLianUrl(String lianUrl) {
        this.lianUrl = lianUrl;
    }

    public Object getXdoorName() {
        return xdoorName;
    }

    public void setXdoorName(Object xdoorName) {
        this.xdoorName = xdoorName;
    }

    public Object getBindingName() {
        return bindingName;
    }

    public void setBindingName(Object bindingName) {
        this.bindingName = bindingName;
    }

    public Object getBindingType() {
        return bindingType;
    }

    public void setBindingType(Object bindingType) {
        this.bindingType = bindingType;
    }

    public Object getBindingId() {
        return bindingId;
    }

    public void setBindingId(Object bindingId) {
        this.bindingId = bindingId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FaceUrlBean{" +
                "lianYezhuid=" + lianYezhuid +
                ", xdoorBh='" + xdoorBh + '\'' +
                ", chiyouren='" + chiyouren + '\'' +
                ", xdoorLianId=" + xdoorLianId +
                ", yezhuId=" + yezhuId +
                ", gongsiBh='" + gongsiBh + '\'' +
                ", xiangmuBh='" + xiangmuBh + '\'' +
                ", yezhuPhone='" + yezhuPhone + '\'' +
                ", shanchu=" + shanchu +
                ", bangdingTime='" + bangdingTime + '\'' +
                ", xiangmuName=" + xiangmuName +
                ", gongsiName=" + gongsiName +
                ", lianName='" + lianName + '\'' +
                ", lianBinUrl='" + lianBinUrl + '\'' +
                ", lianUrl='" + lianUrl + '\'' +
                ", xdoorName=" + xdoorName +
                ", bindingName=" + bindingName +
                ", bindingType=" + bindingType +
                ", bindingId=" + bindingId +
                ", fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
