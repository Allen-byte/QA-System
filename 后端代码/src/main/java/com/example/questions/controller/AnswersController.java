package com.example.questions.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.common.MessageSet;
import com.example.questions.common.Result;
import com.example.questions.entity.*;
import com.example.questions.service.AnswersService;
import com.example.questions.service.MessagesService;
import com.example.questions.service.QuestionsService;
import com.example.questions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author oliver
 * @since 2023-07-05
 */
@RestController
@CrossOrigin
@RequestMapping("/answers")

public class AnswersController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AnswersService answersService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionsService questionsService;

    @Autowired
    private MessagesService messagesService;


    @PostMapping("/getAnswersInfo")
    public Result getAnswersInfo(@RequestBody HashMap<String, String> hashMap){
        Integer uid = Integer.parseInt(hashMap.get("uid"));
        Integer limit = 10;

        List<recordsA> list = answersService.getRecords(limit, uid);
        if(list.size()>0){
            HashMap<String, Object> resData = new HashMap<>();
            ArrayList<String> dates = new ArrayList<>();
            ArrayList<Integer> counts = new ArrayList<>();
            String year = list.get(0).getAnswerDate().substring(0, 4);    //获取日期年份
            for(recordsA record : list){
                dates.add(record.getAnswerDate().substring(5));
                counts.add(record.getCount());
            }
            int total = answersService.lambdaQuery().eq(Answers::getUid, uid).list().size();
            resData.put("dates", dates);
            resData.put("counts", counts);
            resData.put("total", total);
            resData.put("year", year);
            return Result.success(1, resData);
        }else{
            return Result.fail("暂无回答");
        }
    }

    //根据uid获取用户回答
    @GetMapping("/myanswers")
    public Result myanswers(@RequestParam HashMap<String, String> hashMap){
        Page<AnswerShow> page = new Page<>();
        page.setCurrent(Integer.parseInt(hashMap.get("pageNum")));
        page.setSize(Integer.parseInt(hashMap.get("pageSize")));
        IPage<AnswerShow> iPage = answersService.getMyAnswers(page, Integer.parseInt(hashMap.get("uid")));
        return Result.success(iPage.getTotal(), iPage.getRecords());
    }

    //查看用户提问下的全部回答，自定义分页，也可用于提问圈提问的回答查询
    @PostMapping("/query")
    public Result query(@RequestBody HashMap<String, String> hashMap){
        Page<viewAnswers> page = new Page<>();
        page.setCurrent(Integer.parseInt(hashMap.get("pageNum")));
        page.setSize(Integer.parseInt(hashMap.get("pageSize")));
        IPage<viewAnswers> iPage = answersService.getAll(page, Integer.parseInt(hashMap.get("qid")));
        return iPage.getTotal()==0?Result.fail("没有更多回答"):Result.success(iPage.getTotal(), iPage.getRecords());
    }

    //更新回答
    @PostMapping("/update")
    public Result update(@RequestBody Answers answers){
        MessageSet.insert(messagesService, userService, questionsService, answers, 1);
        return answersService.updateById(answers)?Result.success(0, null):Result.fail("更新失败");
    }

    // 删除回答
    @GetMapping("/del/{id}")
    public Result del(@PathVariable String id){
        List<Answers> answers = answersService.lambdaQuery().eq(Answers::getId, id).list();
        MessageSet.insert(messagesService, userService, questionsService, answers.get(0), 2);
        return answersService.removeById(id)?Result.success(0, null):Result.fail("删除问题失败");
    }

    //管理端回答删除
    @GetMapping("/remove/{id}")
    public Result removeAnswer(@PathVariable("id") String id){
        return answersService.removeById(id)?Result.success(0, null):Result.fail("删除问题失败");
    }

    //添加回答
    @PostMapping("/add")
    public Result add(@RequestBody Answers answers){
        MessageSet.insert(messagesService, userService, questionsService, answers, 0);
        return answersService.save(answers)?Result.success(0, null):Result.fail("回答失败");
    }

    //管理端获取全部回答以及对应的提问信息、回答信息
    @GetMapping("/all")
    public Result all(@RequestParam HashMap<String, String> params){
        Page<answerAll> page = new Page<>();
        page.setSize(Integer.parseInt(params.get("pageSize")));
        page.setCurrent(Integer.parseInt(params.get("pageNum")));
        QueryWrapper<answerAll> query = new QueryWrapper<>();
        String content = params.get("content");
        String date = params.get("date");
        String username = params.get("username");
        if(StringUtils.isNotBlank(content)){
            query.like("t1.answer", content);
        }
        if(StringUtils.isNotBlank(date)){
            query.like("t1.answer_date", date);
        }
        if(StringUtils.isNotBlank(username)){
            query.like("t3.username", username);
        }
        IPage<answerAll> iPage = answersService.answersAll(page, query);
        return iPage.getTotal()>0?Result.success(iPage.getTotal(), iPage.getRecords()):Result.fail("暂无数据");
    }

}
