package com.cxwl.menjin.lock.entity;

/**
 * Created by William on 2019/12/1.
 */
public class CheckFaceBean {

    /**
     * result : -1
     * phone :
     * open : false
     */

    private String result;//有结果1 没结果-1
    private String phone;
    private boolean open;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
