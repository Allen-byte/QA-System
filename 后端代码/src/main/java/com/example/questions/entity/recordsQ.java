package com.example.questions.entity;

import lombok.Data;

//接收提问日期和次数的查询结果
@Data
public class recordsQ {
    private String poseDate;      //和查询结果的字段对应上,表中下划线需要写成驼峰表示
    private Integer count;
}
