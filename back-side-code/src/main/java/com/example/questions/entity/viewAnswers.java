package com.example.questions.entity;

import lombok.Data;

//用于接收查询某一个问题下的全部回答
@Data
public class viewAnswers {

    private Integer id;
    private Integer permission;
    private String answer;
    private String answerTime;
    private String username;
    private String avatar;
}
