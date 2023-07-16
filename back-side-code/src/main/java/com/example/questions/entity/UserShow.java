package com.example.questions.entity;

import lombok.Data;

@Data
public class UserShow {
    private Integer uid;
    private String account;
    private String username;
    private String gender;
    private String loginTime;
    private String registerTime;
    private String avatar;
}
