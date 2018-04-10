package com.example.administrator.myone;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Administrator on 2017/7/22.
 */

public class MyBmobInstallation extends BmobInstallation {

    private BmobGeoPoint location;

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }
}
