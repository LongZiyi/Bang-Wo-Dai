package com.example.administrator.myone;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/7/14.
 */

public class User extends BmobUser {
    private String userPhone;
    private int userCreditValue;

    public int getUserCreditValue() {
        return userCreditValue;
    }

    public void setUserCreditValue(int userCreditValue) {
        this.userCreditValue = userCreditValue;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

}
