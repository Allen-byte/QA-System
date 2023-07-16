package com.example.questions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.*;
import com.example.questions.mapper.AnswersMapper;
import com.example.questions.service.AnswersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author oliver
 * @since 2023-07-05
 */
@Service
public class AnswersServiceImpl extends ServiceImpl<AnswersMapper, Answers> implements AnswersService {
    @Autowired
    private AnswersMapper answersMapper;

    @Override
    public List<recordsA> getRecords(Integer limit, Integer uid){
        return answersMapper.getRecords(limit, uid);
    }

    @Override
    public IPage<AnswerShow> getMyAnswers(Page<AnswerShow> page, Integer uid){
        return answersMapper.getMyAnswers(page, uid);
    }

    @Override
    public IPage<viewAnswers> getAll(Page<viewAnswers> page, Integer qid){
        return answersMapper.getAll(page, qid);
    }

    @Override
    public IPage<answerAll> answersAll(Page<answerAll> page, QueryWrapper<answerAll> wrapper){
        return answersMapper.answersAll(page, wrapper);
    }
}
