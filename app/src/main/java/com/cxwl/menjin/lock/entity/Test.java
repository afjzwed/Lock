package com.cxwl.menjin.lock.entity;

/**
 * Created by William on 2019/5/11.
 */

public class Test {

    /**
     * code : 0
     * msg : 成功！
     * data : {"id":8,"xiangmuBh":"xm00000029","gongsiBh":"gs000018","tishiyinUrl":"WJDC/WJDC_34889541.mp4",
     * "huamianUrl":"WJDC/WJDC_93578058.mp4"}
     */

    private String code;
    private String msg;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
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
    }
}
