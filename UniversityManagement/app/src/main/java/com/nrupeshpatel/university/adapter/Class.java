package com.nrupeshpatel.university.adapter;

public class Class {

    private String title, id, semester, students;

    public Class() {
    }

    public Class(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public Class(String title, String id, String semester, String students) {
        this.title = title;
        this.id = id;
        this.semester = semester;
        this.students = students;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getStudents() {
        return students;
    }

    public void setStudents(String students) {
        this.students = students;
    }

}