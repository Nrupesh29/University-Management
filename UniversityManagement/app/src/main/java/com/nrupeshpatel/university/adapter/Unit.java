package com.nrupeshpatel.university.adapter;

public class Unit {

    private String title, code, name, status;

    public Unit() {
    }

    public Unit(String name, String title, String code, String status) {
        this.name = name;
        this.title = title;
        this.code = code;
        this.status = status;
    }

    public Unit(String name, String title, String code) {
        this.name = name;
        this.title = title;
        this.code = code;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}