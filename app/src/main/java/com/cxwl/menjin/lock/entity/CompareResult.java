package com.cxwl.menjin.lock.entity;

/**
 * Created by William on 2020/3/19.
 */

public class CompareResult {
    private String userName;
    private float similar;

    public CompareResult(String userName, float similar) {
        this.userName = userName;
        this.similar = similar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }
}
