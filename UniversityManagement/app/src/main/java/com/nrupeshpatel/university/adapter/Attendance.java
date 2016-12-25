package com.nrupeshpatel.university.adapter;

public class Attendance {

    private String total, name, code, lecture;

    public Attendance() {
    }

    public Attendance(String total, String name, String code, String lecture) {
        this.total = total;
        this.name = name;
        this.code = code;
        this.lecture = lecture;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
    }

}