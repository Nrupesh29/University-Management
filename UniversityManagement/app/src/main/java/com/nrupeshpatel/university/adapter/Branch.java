package com.nrupeshpatel.university.adapter;

public class Branch {
    
    private String title, code, semester;

    public Branch() {
    }

    public Branch(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public Branch(String title, String code, String semester) {
        this.title = title;
        this.code = code;
        this.semester = semester;
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

}