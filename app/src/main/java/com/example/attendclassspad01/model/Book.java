package com.example.attendclassspad01.model;

/**
 * 错题本、书本模型类
 */
public class Book {
    private String ID = "";//ID
    private String name = "";//名称
    private String subjectName = "";//所属科目
    private int resID;//图片资源ID

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }
}
