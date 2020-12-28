package com.example.spkr;

import java.io.Serializable;

public class LaptopCheckOutInfo implements Serializable {

    private String serialNo =  "testingLaptopCheckout";
    private String registrationNo = "Laptop Checkout";
    private String laptopID = "1234LaptopCheckout";
    private String status = "";
    private String studentName = "";
    private String studentIC = "";
    private String studentClass = "";
    private String checkoutDate = "";
    private String returnDate = "";

    public LaptopCheckOutInfo () {};
    public LaptopCheckOutInfo (LaptopInfo laptopInfo, StudentInfo studentInfo) {
        this.serialNo = laptopInfo.getSerialNo();
        this.registrationNo = laptopInfo.getRegistrationNo();
        this.laptopID = laptopInfo.getLaptopID();
        this.status = laptopInfo.getStatus();
        this.studentName = studentInfo.getStudentName();
        this.studentClass = studentInfo.getStudentClass();
        this.studentIC = studentInfo.getStudentIC();
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentIC() {
        return studentIC;
    }

    public void setStudentIC(String studentIC) {
        this.studentIC = studentIC;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(String checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

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

}
