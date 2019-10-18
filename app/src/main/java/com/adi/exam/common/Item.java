package com.adi.exam.common;

import java.util.Hashtable;

public class Item extends Hashtable {

    public Item() {

    }

    public void setAttribute(String name, String value) {
        this.put(name, value);
    }

    public String getAttribute(String key) {

        return this.get(key) != null ? this.get(key).toString() : "";

    }

    public int getId() {
        return 0;
    }

}