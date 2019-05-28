package com.example.thi;

import java.util.ArrayList;

public class Question {
    //tạo class Question
    private String id;
    private String question;
    private String img;
    private String ST1;
    private String ST2;
    private String ST3;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }


    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getST1() {
        return ST1;
    }

    public void setST1(String ST1) {
        this.ST1 = ST1;
    }

    public String getST2() {
        return ST2;
    }

    public void setST2(String ST2) {
        this.ST2 = ST2;
    }

    public String getST3() {
        return ST3;
    }

    public void setST3(String ST3) {
        this.ST3 = ST3;
    }

    public Question() {
    }
}
