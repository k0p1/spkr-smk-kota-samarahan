package com.example.spkr;

import java.io.Serializable;

public class LaptopInfo extends LaptopCheckOutInfo implements Serializable, DAO {

    private String serialNo =  "testingLaptopInfo";
    private String registrationNo = "LaptopInfo";
    private String laptopID = "1234LaptopInfo";
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

    @Override
    public void setData (String a) {
        //do all the ifs (regex) to determine fall into what category
        if(a.startsWith("NXMZDSM0156320FC")) {
            this.serialNo = a;
        }

        else if (a.startsWith("YEA")) {
            this.registrationNo = a;
        }

        else if (a.length() <= 3) {
            this.laptopID = a;
        }
    }
}
