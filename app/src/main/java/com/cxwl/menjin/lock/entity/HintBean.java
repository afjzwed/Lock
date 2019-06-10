package com.cxwl.menjin.lock.entity;

/**
 * Created by William on 2019/5/22.
 */

public class HintBean {
    /**
     * id : 8
     * xiangmuBh : xm00000029
     * gongsiBh : gs000018
     * tishiyinUrl : WJDC/WJDC_34889541.mp4
     * huamianUrl : WJDC/WJDC_93578058.mp4
     */

    private int id;
    private String xiangmuBh;
    private String gongsiBh;
    private String tishiyinUrl;
    private String huamianUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXiangmuBh() {
        return xiangmuBh;
    }

    public void setXiangmuBh(String xiangmuBh) {
        this.xiangmuBh = xiangmuBh;
    }

    public String getGongsiBh() {
        return gongsiBh;
    }

    public void setGongsiBh(String gongsiBh) {
        this.gongsiBh = gongsiBh;
    }

    public String getTishiyinUrl() {
        return tishiyinUrl;
    }

    public void setTishiyinUrl(String tishiyinUrl) {
        this.tishiyinUrl = tishiyinUrl;
    }

    public String getHuamianUrl() {
        return huamianUrl;
    }

    public void setHuamianUrl(String huamianUrl) {
        this.huamianUrl = huamianUrl;
    }

    @Override
    public String toString() {
        return "HintBean{" +
                "id=" + id +
                ", xiangmuBh='" + xiangmuBh + '\'' +
                ", gongsiBh='" + gongsiBh + '\'' +
                ", tishiyinUrl='" + tishiyinUrl + '\'' +
                ", huamianUrl='" + huamianUrl + '\'' +
                '}';
    }
}
