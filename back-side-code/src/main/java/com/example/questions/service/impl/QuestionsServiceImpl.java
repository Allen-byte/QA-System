package com.example.questions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.QuestionShow;
import com.example.questions.entity.recordsQ;
import com.example.questions.entity.Questions;
import com.example.questions.mapper.QuestionsMapper;
import com.example.questions.service.QuestionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author oliver
 * @since 2023-06-28
 */
@Service
public class QuestionsServiceImpl extends ServiceImpl<QuestionsMapper, Questions> implements QuestionsService {
    @Autowired
    private QuestionsMapper questionsMapper;


    @Override
    public List<recordsQ> getRecords(Integer limit, Integer uid){
        return questionsMapper.getRecords(limit, uid);
    }


    @Override
    public IPage<QuestionShow> queryInfo(Page<QuestionShow> page, QueryWrapper<QuestionShow> wrapper){
        return questionsMapper.queryInfo(page, wrapper);
    }
}
