package com.example.spkr;

public class LaptopCheckOutInfo {

    LaptopInfo laptopInfo;
    StudentInfo studentInfo;

    private String serialNo =  "testingLaptopCheckout";
    private String registrationNo = "Laptop Checkout";
    private String laptopID = "1234LaptopCheckout";
    private String status;

    public String getLaptopID() {
        return laptopID;
    }

    public void setLaptopID(String laptopID) {
        this.laptopID = laptopID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSerialNo () {
        return this.serialNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getRegistrationNo  () {
        return this.registrationNo;
    }

    public void setLaptopInfo(LaptopInfo laptopInfo) {
        this.laptopInfo = laptopInfo;
    }

    public LaptopInfo getLaptopInfo() {
        return laptopInfo;
    }

    private String checkoutTime = "";
    private String returnedTime = "";
}
