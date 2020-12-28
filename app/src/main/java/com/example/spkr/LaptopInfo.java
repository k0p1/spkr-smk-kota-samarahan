package com.example.spkr;

import java.io.Serializable;

public class LaptopInfo implements Serializable, DAO {

    private String serialNo =  "testingLaptopInfo";
    private String registrationNo = "LaptopInfo";
    private String laptopID = "1234LaptopInfo";
    private String status;

    public LaptopInfo () {};
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
    public void setData (String [] a) {
        //do all the ifs (regex) to determine fall into what category
        for (int i = 0; i < a.length; i++) {
            if (a[i].startsWith("NXMZDSM0156320FC")) {
                this.serialNo = a[i];
            } else if (a[i].startsWith("YEA")) {
                this.registrationNo = a[i];
            } else if (a[i].matches("\\d*")) {
                this.laptopID = a[i];
            }
        }
    }
}
