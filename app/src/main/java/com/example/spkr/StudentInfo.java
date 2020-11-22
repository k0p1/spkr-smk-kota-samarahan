package com.example.spkr;

import java.io.Serializable;

public class StudentInfo extends LaptopCheckOutInfo implements Serializable, DAO {
    private String studentName = "";
    private String studentIC = "";

    @Override
    public void setData(String data) {

    }
}

