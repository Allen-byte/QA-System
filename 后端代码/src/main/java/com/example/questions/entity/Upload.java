package com.example.questions.entity;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


//接收前端发送的头像数据
@Data
public class Upload {
    private MultipartFile file;
    private String uid;
    private String updatedTime;
}
