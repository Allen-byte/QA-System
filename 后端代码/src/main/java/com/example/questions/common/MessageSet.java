package com.example.questions.common;

import com.example.questions.entity.Answers;
import com.example.questions.entity.Messages;
import com.example.questions.entity.Questions;
import com.example.questions.entity.User;
import com.example.questions.service.MessagesService;
import com.example.questions.service.QuestionsService;
import com.example.questions.service.UserService;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


// 用于设置消息插入等操作
@Data
public class MessageSet {


    public static void insert(MessagesService messagesService, UserService userService, QuestionsService questionsService, Answers answers, int type){
        String message = "";
        //插入消息记录
        //获取用户名
        List<User> listU = userService.lambdaQuery().eq(User::getUid, answers.getUid()).list();
        String username = listU.get(0).getUsername();
        //获取问题
        List<Questions> listQ = questionsService.lambdaQuery().eq(Questions::getId, answers.getQid()).list();
        String title = listQ.get(0).getTitle();
        //获取提问者uid
        Integer uid = listQ.get(0).getUid();
        if(type==0){                      //新增
            message = "【" + title + "】 " + username + " 回答了该问题";
        }else if(type == 1){             //更新
            message = "【" + title + "】 " + username + " 更新了该问题下的回答";
        }else if(type == 2){             //删除
            message = "【" + title + "】 " + username + " 删除了该问题下的回答";
        }

        //设置消息发送时间
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        String time = formatter.format(date);

        Messages messages = new Messages();
        messages.setMessage(message);
        messages.setUid(uid);
        messages.setTime(time);
        messages.setDate(time.split(" ")[0]);     //设置日期
        messages.setFlag(0);      //设为未读

        if(messagesService.save(messages)){
            System.out.println("消息发送成功");
        }else{
            System.out.println("消息发送失败");
        }
    }
}
