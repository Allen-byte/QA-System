package com.example.questions.common;

import lombok.Data;

@Data
public class Result {
    private int code;      //200:成功，201：失败
    private String msg;   //状态消息
    private long total;  //适用于返回时的总查询结果条数
    private Object data; //具体数据，一般是对象数组：[{},{}...]

    private static Result basic(int code, String msg, long total, Object data){
        Result res = new Result();
        res.setCode(code);
        res.setMsg(msg);
        res.setTotal(total);
        res.setData(data);
        return res;
    }

    //成功
    public static Result success(long total, Object data){
        return basic(200, "操作成功", total, data);
    }

    //失败
    public static Result fail(String msg){
        return basic(201, msg, 0, null);
    }
}
