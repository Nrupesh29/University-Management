package com.nrupeshpatel.university.adapter;

public class StudentAttendance {

    private String title, code, attendance;

    public StudentAttendance() {
    }

    public StudentAttendance(String title, String code, String attendance) {
        this.title = title;
        this.code = code;
        this.attendance = attendance;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return code;
    }

    public String getAttendance() {
        return attendance;
    }

}