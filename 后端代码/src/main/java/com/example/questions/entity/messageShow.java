package com.example.questions.entity;

import lombok.Data;

@Data
public class messageShow {
    private Integer mid;
    private Integer uid;
    private String username;
    private String account;
    private String message;
    private Integer flag;
    private String time;
}
