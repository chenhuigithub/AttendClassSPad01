package com.example.attendclassspad01.model;

/**
 * 班级模型类
 */
public class Classes {
    private String ID;
    private String name;
    private String hasChoiced;//是否被选中

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

    public String getHasChoiced() {
        return hasChoiced;
    }

    public void setHasChoiced(String hasChoiced) {
        this.hasChoiced = hasChoiced;
    }
}
