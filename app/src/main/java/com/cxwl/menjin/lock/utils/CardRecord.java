package com.cxwl.menjin.lock.utils;

import java.util.Date;

public class CardRecord {
    public String card = null;
    public Date creDate = null;

    public String name = null;
    public Date nameDate = null;

    public String body = null;
    public Date bodyDate = null;

    public CardRecord() {
        this.card = "";
        this.creDate = new Date();

        this.name = "";
        this.nameDate = new Date();

        this.body = "1";
        this.bodyDate = new Date();
    }

    public boolean checkLastCard(String card) {
        boolean result = false;
        if (this.card.equals(card)) {
            long offset = new Date().getTime() - this.creDate.getTime();
            if (offset > 2000) {
                this.card = card;
                this.creDate = new Date();
            } else {
                result = true;
            }
        } else {
            this.card = card;
            this.creDate = new Date();
        }
        return result;
    }

    public boolean checkLastCardNew(String name) {
        boolean result = false;
        if (this.name.equals(name)) {
            long offset = new Date().getTime() - this.nameDate.getTime();
            if (offset > 1000 * 8) {
                this.name = name;
                this.nameDate = new Date();
            } else {
                result = true;
            }
        } else {
            this.name = name;
            this.nameDate = new Date();
        }
        return result;
    }

    public boolean checkLastBody(String body) {
        boolean result = false;
        if (this.body.equals(body)) {
            long offset = new Date().getTime() - this.bodyDate.getTime();
            if (offset > 1000 * 7) {
                this.body = body;
                this.bodyDate = new Date();
            } else {
                result = true;
            }
        } else {
            this.body = body;
            this.bodyDate = new Date();
        }
        return result;
    }
}
