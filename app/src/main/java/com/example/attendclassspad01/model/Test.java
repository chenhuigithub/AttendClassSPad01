package com.example.attendclassspad01.model;

import java.io.Serializable;

/**
 * 题目模型类
 *
 * @author chenhui 2019.04.04
 */
public class Test implements Serializable {
    private String id = "";// 唯一ID
    private String title = "";// 题目标题
    private String content = "";// 题目内容
    private String userAnswer = "";// 用户答案id
    private String rightAnswer = "";// 正确答案id
    private String analysis = "";// 解析
    private boolean isChoiced = false;// 是否已被选中

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public boolean isChoiced() {
        return isChoiced;
    }

    public void setChoiced(boolean isChoiced) {
        this.isChoiced = isChoiced;
    }

}
