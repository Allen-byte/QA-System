package com.example.questions.controller;


import com.example.questions.common.Result;
import com.example.questions.entity.Avatar;
import com.example.questions.entity.Upload;
import com.example.questions.entity.User;
import com.example.questions.service.AvatarService;
import com.example.questions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author oliver
 * @since 2023-06-29
 */
@RestController
@CrossOrigin
@RequestMapping("/avatar")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    //登陆时获取头像
    @GetMapping("/getAvatar")
    public Result getAvatar(@RequestParam("account") String account){
//        根据account获取用户
        List<User> list = userService.lambdaQuery().eq(User::getAccount, account).list();
        if(list.size()==0){
            return Result.fail("用户不存在");
        }
//        根据uid获取Avatar对象
        List<Avatar> avatar = avatarService.lambdaQuery().eq(Avatar::getUid, list.get(0).getUid()).list();
        if(avatar.size()==0){
            return Result.fail("暂无头像");
        }
        String avatarPath = avatar.get(0).getAvatar();
        return Result.success(1, avatarPath);
    }

    //    接收头像
    @PostMapping("/upload")
    public Result avatar(Upload upload) throws FileNotFoundException {
        MultipartFile avatarFile = upload.getFile();
        String uid = upload.getUid();
        String updatedTime = upload.getUpdatedTime();
        if(avatarFile.isEmpty()){
            return Result.fail("文件上传失败");
        }

        // 设置文件名：uid+原文件名
        String fileName = uid + "_" + avatarFile.getOriginalFilename();
        //文件保存路径
//        String path = ResourceUtils.getURL("classpath:").getPath();  //部署到线上时失效
        String path = System.getProperty("user.dir") + "/upload";

        //头像保存目录路径
        File avatarDir = new File(path + "/avatar");
        //如果文件路径不存在，就新建一个
        if(!avatarDir.exists()){
            System.out.println("正在创建:" + path + "/avatar目录");
            avatarDir.mkdirs();
        }

        //实际的文件地址
        File dest = new File(avatarDir + System.getProperty("file.separator") + fileName);
        //数据库路径
        String storePath = "/static/upload/avatar/" + fileName;

        try{
            //保存图片
            avatarFile.transferTo(dest);
            //更新数据库
            Avatar avatar = new Avatar();
            avatar.setUid(Integer.valueOf(uid));
            avatar.setAvatar(storePath);
            avatar.setUpdateTime(updatedTime);
            //判断头像记录是否存在
            List<Avatar> list = avatarService.lambdaQuery()
                    .eq(Avatar::getUid, uid)
                    .list();
            //不存在调用save新增，否则使用自定义的sql修改
            //默认的saveOrUpdate方法每修改一次就会新增记录，因为在avatar表中uid不是主键
            if(list.size()==0){
                return avatarService.save(avatar)?Result.success(1, storePath):Result.fail("文件上传失败");
            }else{
                return avatarService.updateAvatar(avatar)>0?Result.success(1, storePath):Result.fail("文件上传失败");
            }

        }catch(IOException e){
            e.printStackTrace();
            return Result.fail("文件上传失败");
        }
    }
}
