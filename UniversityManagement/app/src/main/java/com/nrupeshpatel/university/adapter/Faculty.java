package com.nrupeshpatel.university.adapter;

public class Faculty {

    private String title, code;

    public Faculty() {
    }

    public Faculty(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getTitle() {
        return title;
    }
    public String getCode() {
        return code;
    }

    public void setTitle(String name) {
        this.title = name;
    }
    public void setCode(String code) {
        this.code = code;
    }

}