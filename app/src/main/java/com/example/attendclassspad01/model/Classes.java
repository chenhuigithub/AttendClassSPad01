package com.example.attendclassspad01.model;

/**
 * 班级模型类
 */
public class Classes {
    private String ID;
    private String name;
    private boolean hasChoiced;//是否被选中

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

    public boolean isHasChoiced() {
        return hasChoiced;
    }

    public void setHasChoiced(boolean hasChoiced) {
        this.hasChoiced = hasChoiced;
    }
}
