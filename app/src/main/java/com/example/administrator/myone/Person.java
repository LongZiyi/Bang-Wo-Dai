package com.example.administrator.myone;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Administrator on 2017/7/16.
 */

public class Person extends BmobObject{

    private String userName;
    private Boolean isOpen;
    private BmobGeoPoint gpsAdd;

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BmobGeoPoint getGpsAdd() {
        return gpsAdd;
    }
    public void setGpsAdd(BmobGeoPoint gpsAdd) {
        this.gpsAdd = gpsAdd;
    }
}