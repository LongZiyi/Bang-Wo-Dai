package com.example.administrator.myone;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/7/5.
 */

public class Order extends BmobObject{
    private String orderName;
    private String orderPrice;
    private String orderAddress;
    private String orderDestination;
    private String orderRemark;
    private String orderTime;
    private String orderAddresser;     //发件人
    private String orderFirstDes;
    private String orderCourier;       //快递员
    private String orderRecipient;     //收件人
    private String city;
    private String orderStatus; //订单状态

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderDestination() {
        return orderDestination;
    }

    public void setOrderDestination(String orderDestination) {
        this.orderDestination = orderDestination;
    }

    public String getOrderRemark() {
        return orderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        this.orderRemark = orderRemark;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderAddresser() {
        return orderAddresser;
    }

    public void setOrderAddresser(String orderAddresser) {
        this.orderAddresser = orderAddresser;
    }

    public String getOrderFirstDes() {
        return orderFirstDes;
    }

    public void setOrderFirstDes(String orderFirstDes) {
        this.orderFirstDes = orderFirstDes;
    }

    public String getOrderCourier() {
        return orderCourier;
    }

    public void setOrderCourier(String orderCourier) {
        this.orderCourier = orderCourier;
    }

    public String getOrderRecipient() {
        return orderRecipient;
    }

    public void setOrderRecipient(String orderRecipient) {
        this.orderRecipient = orderRecipient;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
