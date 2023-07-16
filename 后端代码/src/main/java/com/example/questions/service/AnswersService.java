package com.example.questions.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author oliver
 * @since 2023-07-05
 */
public interface AnswersService extends IService<Answers> {
    //获取个人中心首页回答的绘图数据
    public List<recordsA> getRecords(Integer limit, Integer uid);

    //获取用户回答记录的数据,自定义分页
    public IPage<AnswerShow> getMyAnswers(Page<AnswerShow> page, Integer uid);

    //根据qid获取某一问题下的全部回答
    public IPage<viewAnswers> getAll(Page<viewAnswers> page, Integer qid);

    //获取回答信息以及相关的提问和用户信息
    public IPage<answerAll> answersAll(Page<answerAll> page, QueryWrapper<answerAll> wrapper);
}
