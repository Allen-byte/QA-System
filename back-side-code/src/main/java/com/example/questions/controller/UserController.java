package com.example.questions.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.common.MD5;
import com.example.questions.common.Result;
import com.example.questions.entity.*;
import com.example.questions.service.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private QuestionsService questionsService;

    @Autowired
    private AnswersService answersService;

    @Autowired
    private MessagesService messagesService;

    // 用户注册
    @PostMapping("/register")
    public Result register(@RequestBody User user){
        //md5加密
        String rawPwd = user.getPassword();
        String md5 = MD5.generate(rawPwd);
        user.setPassword(md5);
        return userService.save(user)?Result.success(0, null):Result.fail("注册失败");
    }

    //账号唯一性检测
    @GetMapping("/check")
    public Result check(@RequestParam(value = "account") String account){
        List<User> list = userService.lambdaQuery().eq(User::getAccount, account).list();
        return list.size()>0?Result.fail("唯一性检测不通过"):Result.success(0, null);
    }


    //用户登录检查并返回对应的权限菜单
    @PostMapping("/login")
    public Result login(@RequestBody HashMap<String, String> hashMap){
        String account = hashMap.get("account");
        List<User> list = userService.lambdaQuery()
                .eq(User::getAccount, account)
                .list();
        if(list.size()==0){
            return Result.fail("该用户未注册");
        }else{
            String password = hashMap.get("password");
            String loginTime = hashMap.get("loginTime");
            User user = list.get(0);
            //验证成功
            if(MD5.verify(password, user.getPassword())){
                //根据用户获取对应的菜单信息, permission为数字需要转为字符串
                List<Menu> menu = menuService.lambdaQuery().like(Menu::getMenuright, user.getPermission() + "").list();
                HashMap<String, Object> resData = new HashMap<>();
                resData.put("menu", menu);
                resData.put("user", user);
//                更新用户登录时间信息
                UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("uid", user.getUid());
                updateWrapper.set("login_time", loginTime);
                userService.update(updateWrapper);
                //设置用户的默认头像,如果存在头像则不设置
                List<Avatar> avatar = avatarService.lambdaQuery().eq(Avatar::getUid, user.getUid()).list();
                if(avatar.size()==0){
                    Avatar ava = new Avatar();
                    ava.setUpdateTime(loginTime);
                    ava.setUid(user.getUid());
                    ava.setAvatar("/static/default.jpg");
                    avatarService.save(ava);
                }
                return Result.success(resData.size(), resData);
            }
            return Result.fail("用户名或密码错误");
        }
    }

    // 获取全部用户
    @PostMapping("/all")
    public Result all(@RequestBody HashMap<String, String> hashMap){
        int pageSize = Integer.parseInt(hashMap.get("pageSize"));
        int pageNum = Integer.parseInt(hashMap.get("pageNum"));
        QueryWrapper<User> query = new QueryWrapper<>();
        String username = hashMap.get("username");
        String gender = hashMap.get("gender");
        if(StringUtils.isNotBlank(username)){
            query.like("t1.username", username);
        }
        if(StringUtils.isNotBlank(gender)){
            query.like("t1.gender", gender);
        }
        query.eq("t1.permission", 0);                   //查询全部的普通用户
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> iPage = userService.userShow(page, query);
        return iPage.getTotal()>0?Result.success(iPage.getTotal(), iPage.getRecords()):Result.fail("暂无数据");
    }

    //更新用户信息
    @PostMapping("/update")
    public Result update(@RequestBody User user){
        //如果user.password长度不超过16位，说明是原文密码，因为加密的密码长度为64位，则要进行加密
        if(user.getPassword().length()<=16) {
            String rawPwd = user.getPassword();
            String md5 = MD5.generate(rawPwd);
            user.setPassword(md5);
        }
        return userService.updateById(user)?Result.success(0, null):Result.fail("更新失败");
    }

    //删除用户
    @GetMapping("/del/{id}")
    public Result delUser(@PathVariable("id") Integer id){
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", id);
        //先看用户是否存在回答
        List<Answers> answers = answersService.lambdaQuery().eq(Answers::getUid, id).list();
        //先看用户是否存在问题
        List<Questions> questions = questionsService.lambdaQuery().eq(Questions::getUid, id).list();
        //用户是否存在消息记录
        List<Messages> messages = messagesService.lambdaQuery().eq(Messages::getUid, id).list();
        if(answers.size()!=0&&!answersService.removeByMap(map)){
            return Result.fail("删除用户下的回答失败");
        }
        if(questions.size()!=0&&!questionsService.removeById(map)){
            return Result.fail("删除用户下的提问失败");
        }
        if(messages.size()!=0&&!messagesService.removeByMap(map)){
            return Result.fail("删除用户下的消息失败");
        }
        //删除用户时，需要把该用户下的消息、提问和回答都删除
        return userService.removeById(id)?Result.success(0, null):Result.fail("删除失败");
    }

}
