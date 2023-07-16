package com.example.questions.entity;

import lombok.Data;

import java.util.ArrayList;

//该类用于接收问题展示时的查询结果
@Data
public class QuestionShow {
    private Integer uid;
    private Integer qid;
    private String account;
    private String username;
    private Integer permission;
    private String avatar;
    private String poseTime;
    private String question;
    private Integer count;        //回答个数

    private String description;     //问题描述
}
