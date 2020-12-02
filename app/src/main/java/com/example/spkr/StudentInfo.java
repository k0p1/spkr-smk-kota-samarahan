package com.example.spkr;

import java.io.Serializable;

public class StudentInfo implements Serializable, DAO {
    private String studentName = "";
    private String studentIC = "";
    private String studentClass = "";

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

    @Override
    public void setData(String data) {

    }
}

