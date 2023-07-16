package com.example.questions.entity;

import lombok.Data;

//用于接收用户的回答记录展示结果
@Data
public class AnswerShow {
    private Integer id;        //回答的id
    private Integer uid;
    private Integer qid;
    private String title;
    private String answer;
    private String answerTime;
}
