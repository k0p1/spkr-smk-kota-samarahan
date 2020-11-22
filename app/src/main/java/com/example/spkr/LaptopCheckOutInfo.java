package com.example.spkr;

public class LaptopCheckOutInfo {

    LaptopInfo laptopInfo;
    StudentInfo studentInfo;

    public void setLaptopInfo(LaptopInfo laptopInfo) {
        this.laptopInfo = laptopInfo;
    }

    public LaptopInfo getLaptopInfo() {
        return laptopInfo;
    }

    private String checkoutTime = "";
    private String returnedTime = "";
}
