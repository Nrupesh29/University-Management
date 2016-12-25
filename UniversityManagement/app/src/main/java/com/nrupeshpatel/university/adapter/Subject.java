package com.nrupeshpatel.university.adapter;

public class Subject {

    private String title, code, credit;

    public Subject() {
    }

    public Subject(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public Subject(String title, String code, String credit) {
        this.title = title;
        this.code = code;
        this.credit = credit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

}