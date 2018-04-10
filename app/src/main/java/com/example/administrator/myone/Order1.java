package com.example.administrator.myone;

/**
 * Created by Administrator on 2017/7/24.
 */

public class Order1 {
    private String name;
    private String address;
    private int image;

    Order1(String name,String address,int image){
        this.name=name;
        this.address=address;
        this.image=image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
