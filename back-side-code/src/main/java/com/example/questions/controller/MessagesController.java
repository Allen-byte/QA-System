package com.example.questions.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.common.MessageSet;
import com.example.questions.common.Result;
import com.example.questions.entity.*;
import com.example.questions.service.MessagesService;
import com.example.questions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author oliver
 * @since 2023-07-08
 */
@RestController
@CrossOrigin
@RequestMapping("/messages")
public class MessagesController {
    @Autowired
    private MessagesService messagesService;

    @Autowired
    private UserService userService;


    //获取未读消息数量
    @GetMapping("/getUnread/{uid}")
    public Result getUnread(@PathVariable("uid") Integer uid) {
        List<Messages> messages = messagesService.lambdaQuery()
                .eq(Messages::getUid, uid)
                .list();
        if(messages.size() > 0){
            //筛选未读消息
            List<Messages> unreads = messages.stream().filter(message -> message.getFlag().equals(0)).toList();
            return Result.success(1, unreads.size());
        }else{
            return Result.fail("暂无消息通知");
        }
    }

    //用户端获取对应消息
    @PostMapping("/getMessages")
    public Result getMessage(@RequestBody HashMap<String, Integer> hashMap){
        Integer size = hashMap.get("pageSize");
        Integer num = hashMap.get("pageNum");
        Integer uid = hashMap.get("uid");
        Page<Messages> page = new Page<>(num, size);
        LambdaQueryWrapper<Messages> query = new LambdaQueryWrapper<>();
        query.eq(Messages::getUid, uid);
        query.orderByDesc(Messages::getTime);
        IPage<Messages> iPage = messagesService.page(page, query);
        return iPage.getTotal()>0?Result.success(iPage.getTotal(), iPage.getRecords()):Result.fail("暂无消息");
    }

    //设置消息状态
    @GetMapping("/update/{id}")
    public Result update(@PathVariable("id") Integer id){
        UpdateWrapper<Messages> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("flag", 1);          //设为已读
        return messagesService.update(updateWrapper)?Result.success(0, null):Result.fail("更新失败");
    }

    // 删除消息
    @GetMapping("/del/{id}")
    public Result del(@PathVariable Integer id){
        List<Messages> messages = messagesService.lambdaQuery().eq(Messages::getId, id).list();
        return messagesService.removeById(id)?Result.success(0, null):Result.fail("删除消息失败");
    }

    //管理端获取全部消息
    @PostMapping("/all")
    public Result all(@RequestBody HashMap<String, String> params){
        Page<messageShow> page = new Page<>();
        page.setSize(Integer.parseInt(params.get("pageSize")));
        page.setCurrent(Integer.parseInt(params.get("pageNum")));
        QueryWrapper<messageShow> query = new QueryWrapper<>();
        String date = params.get("date");
        String username = params.get("username");
        if(StringUtils.isNotBlank(date)){
            query.eq("t1.date", date);
        }
        if(StringUtils.isNotBlank(username)){
            query.like("t2.username", username);
        }
        IPage<messageShow> iPage = messagesService.allMessages(page, query);
        return iPage.getTotal()>0?Result.success(iPage.getTotal(), iPage.getRecords()):Result.fail("暂无数据");    }

    //管理端删除消息
    @GetMapping("/remove/{id}")
    public Result remove(@PathVariable Integer id){
        return messagesService.removeById(id)?Result.success(0, null):Result.fail("删除消息失败");
    }

    //管理端发送消息
    @PostMapping("/add")
    public Result add(@RequestBody HashMap<String, String> hashMap){
        List<User> list = userService.lambdaQuery().eq(User::getAccount, hashMap.get("account")).list();
        if(list.size()==0){
            return Result.fail("该用户不存在");
        }
        String message = hashMap.get("message");
        String time = hashMap.get("time");
        Integer uid = list.get(0).getUid();
        Messages messages = new Messages();
        messages.setMessage("【管理员】" + message);
        messages.setFlag(0);
        messages.setTime(time);
        messages.setDate(time.split(" ")[0]);
        messages.setUid(uid);
        return messagesService.save(messages)?Result.success(0, null):Result.fail("消息保存失败");
    }

    //获取全部账号
    @PostMapping("/allAccounts")
    public Result allAccounts(){
        List<User> list = userService.lambdaQuery().like(User::getAccount, "").list();
        return list.size()>0?Result.success(list.size(), list):Result.fail("暂无账号数据");
    }
}
