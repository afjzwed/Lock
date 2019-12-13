package com.cxwl.menjin.lock.entity;

/**
 * Created by William on 2019/5/11.
 */

public class Test {

    /**
     * code : 0
     * msg : 成功！
     * data : {"banben":{"id":11787,"ka":"20191205170151133","ka_gx":"2019-12-05 17:01:51.0",
     * "lian":"20191212144254027","lianGx":"2019-12-12 14:42:54.0","xdoorBh":"xd20191203181455","tonggao":null,
     * "tonggaoGx":null,"guanggao":"20191205175219543","guanggaoGx":"2019-12-05 17:52:19.0","tupian":null,
     * "tupianGx":null,"tishi":null,"tishiGx":null,"xintiaoTime":null},"door":{"xdoorBh":"xd20191203181455",
     * "gongsiBh":"gsa001","xiangmuBh":"xma0324","qiqu":"一期","loudong":"7","danyuan":"1","mac":"8c:f7:10:47:a6:64",
     * "key":null,"name":"测试单元门禁","type":1,"shanchu":1,"gongsiName":null,"version":null,"lixianMima":null,
     * "online":false,"versionId":32,"voice":10,"luminance":50,"xiangmuName":"金秋小区"},
     * "token":"28596ce2-a171-4436-976c-4eaf9be64e5a","version":null,"fuwuqi_shijian":"2019-12-12 17:20:39",
     * "lixian_mima":"841014","xintiao_shijian":60}
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
         * banben : {"id":11787,"ka":"20191205170151133","ka_gx":"2019-12-05 17:01:51.0","lian":"20191212144254027",
         * "lianGx":"2019-12-12 14:42:54.0","xdoorBh":"xd20191203181455","tonggao":null,"tonggaoGx":null,
         * "guanggao":"20191205175219543","guanggaoGx":"2019-12-05 17:52:19.0","tupian":null,"tupianGx":null,
         * "tishi":null,"tishiGx":null,"xintiaoTime":null}
         * door : {"xdoorBh":"xd20191203181455","gongsiBh":"gsa001","xiangmuBh":"xma0324","qiqu":"一期","loudong":"7",
         * "danyuan":"1","mac":"8c:f7:10:47:a6:64","key":null,"name":"测试单元门禁","type":1,"shanchu":1,"gongsiName":null,
         * "version":null,"lixianMima":null,"online":false,"versionId":32,"voice":10,"luminance":50,
         * "xiangmuName":"金秋小区"}
         * token : 28596ce2-a171-4436-976c-4eaf9be64e5a
         * version : null
         * fuwuqi_shijian : 2019-12-12 17:20:39
         * lixian_mima : 841014
         * xintiao_shijian : 60
         */

        private BanbenBean banben;
        private DoorBean door;
        private String token;
        private Object version;
        private String fuwuqi_shijian;
        private String lixian_mima;
        private int xintiao_shijian;

        public BanbenBean getBanben() {
            return banben;
        }

        public void setBanben(BanbenBean banben) {
            this.banben = banben;
        }

        public DoorBean getDoor() {
            return door;
        }

        public void setDoor(DoorBean door) {
            this.door = door;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Object getVersion() {
            return version;
        }

        public void setVersion(Object version) {
            this.version = version;
        }

        public String getFuwuqi_shijian() {
            return fuwuqi_shijian;
        }

        public void setFuwuqi_shijian(String fuwuqi_shijian) {
            this.fuwuqi_shijian = fuwuqi_shijian;
        }

        public String getLixian_mima() {
            return lixian_mima;
        }

        public void setLixian_mima(String lixian_mima) {
            this.lixian_mima = lixian_mima;
        }

        public int getXintiao_shijian() {
            return xintiao_shijian;
        }

        public void setXintiao_shijian(int xintiao_shijian) {
            this.xintiao_shijian = xintiao_shijian;
        }

        public static class BanbenBean {
            /**
             * id : 11787
             * ka : 20191205170151133
             * ka_gx : 2019-12-05 17:01:51.0
             * lian : 20191212144254027
             * lianGx : 2019-12-12 14:42:54.0
             * xdoorBh : xd20191203181455
             * tonggao : null
             * tonggaoGx : null
             * guanggao : 20191205175219543
             * guanggaoGx : 2019-12-05 17:52:19.0
             * tupian : null
             * tupianGx : null
             * tishi : null
             * tishiGx : null
             * xintiaoTime : null
             */

            private int id;
            private String ka;
            private String ka_gx;
            private String lian;
            private String lianGx;
            private String xdoorBh;
            private Object tonggao;
            private Object tonggaoGx;
            private String guanggao;
            private String guanggaoGx;
            private Object tupian;
            private Object tupianGx;
            private Object tishi;
            private Object tishiGx;
            private Object xintiaoTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getKa() {
                return ka;
            }

            public void setKa(String ka) {
                this.ka = ka;
            }

            public String getKa_gx() {
                return ka_gx;
            }

            public void setKa_gx(String ka_gx) {
                this.ka_gx = ka_gx;
            }

            public String getLian() {
                return lian;
            }

            public void setLian(String lian) {
                this.lian = lian;
            }

            public String getLianGx() {
                return lianGx;
            }

            public void setLianGx(String lianGx) {
                this.lianGx = lianGx;
            }

            public String getXdoorBh() {
                return xdoorBh;
            }

            public void setXdoorBh(String xdoorBh) {
                this.xdoorBh = xdoorBh;
            }

            public Object getTonggao() {
                return tonggao;
            }

            public void setTonggao(Object tonggao) {
                this.tonggao = tonggao;
            }

            public Object getTonggaoGx() {
                return tonggaoGx;
            }

            public void setTonggaoGx(Object tonggaoGx) {
                this.tonggaoGx = tonggaoGx;
            }

            public String getGuanggao() {
                return guanggao;
            }

            public void setGuanggao(String guanggao) {
                this.guanggao = guanggao;
            }

            public String getGuanggaoGx() {
                return guanggaoGx;
            }

            public void setGuanggaoGx(String guanggaoGx) {
                this.guanggaoGx = guanggaoGx;
            }

            public Object getTupian() {
                return tupian;
            }

            public void setTupian(Object tupian) {
                this.tupian = tupian;
            }

            public Object getTupianGx() {
                return tupianGx;
            }

            public void setTupianGx(Object tupianGx) {
                this.tupianGx = tupianGx;
            }

            public Object getTishi() {
                return tishi;
            }

            public void setTishi(Object tishi) {
                this.tishi = tishi;
            }

            public Object getTishiGx() {
                return tishiGx;
            }

            public void setTishiGx(Object tishiGx) {
                this.tishiGx = tishiGx;
            }

            public Object getXintiaoTime() {
                return xintiaoTime;
            }

            public void setXintiaoTime(Object xintiaoTime) {
                this.xintiaoTime = xintiaoTime;
            }
        }

        public static class DoorBean {
            /**
             * xdoorBh : xd20191203181455
             * gongsiBh : gsa001
             * xiangmuBh : xma0324
             * qiqu : 一期
             * loudong : 7
             * danyuan : 1
             * mac : 8c:f7:10:47:a6:64
             * key : null
             * name : 测试单元门禁
             * type : 1
             * shanchu : 1
             * gongsiName : null
             * version : null
             * lixianMima : null
             * online : false
             * versionId : 32
             * voice : 10
             * luminance : 50
             * xiangmuName : 金秋小区
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
            private int type;
            private int shanchu;
            private Object gongsiName;
            private Object version;
            private Object lixianMima;
            private boolean online;
            private int versionId;
            private int voice;
            private int luminance;
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
        }
    }
}
