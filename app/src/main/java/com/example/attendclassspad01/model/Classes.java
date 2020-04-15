package com.example.attendclassspad01.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 班级模型类
 */
public class Classes {
    private String ID = "";
    private String name = "";

    private String ivUrl = "";//班级头像网络路径
    private int ivRes; // 班级头像
    private String type = "";
    private int posFocus;

    private boolean hasChoiced = false;//是否被选中

    @JSONField(name = "DataID")
    public String getID() {
        return ID;
    }

    @JSONField(name = "DataID")
    public void setID(String ID) {
        this.ID = ID;
    }

    @JSONField(name = "DataName")
    public String getName() {
        return name;
    }

    @JSONField(name = "DataName")
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
