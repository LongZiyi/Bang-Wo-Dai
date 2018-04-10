package com.example.administrator.myone;


import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/7/31.
 */

public class Chat extends BmobObject {
    String orderAddresser;
    String orderRecipient;
    String message;
    Integer whoSend;
    Integer isRead;
    Integer orders;

    public Chat(String orderAddresser,String orderRecipient,String message,Integer whoSend,Integer isRead){
        this.orderAddresser = orderAddresser;
        this.orderRecipient = orderRecipient;
        this.message = message;
        this.whoSend = whoSend;
        this.isRead = isRead;
    }

    public String getOrderAddresser() {
        return orderAddresser;
    }

    public void setOrderAddresser(String orderAddresser) {
        this.orderAddresser = orderAddresser;
    }

    public String getOrderRecipient() {
        return orderRecipient;
    }

    public void setOrderRecipient(String orderRecipient) {
        this.orderRecipient = orderRecipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getWhoSend() {
        return whoSend;
    }

    public void setWhoSend(Integer whoSend) {
        this.whoSend = whoSend;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }
}
