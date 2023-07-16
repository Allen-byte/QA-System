package com.example.questions.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Wrapper;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author oliver
 * @since 2023-07-05
 */
@Mapper
public interface AnswersMapper extends BaseMapper<Answers> {
    //根据uid获取回答情况
    public List<recordsA> getRecords(@Param("limit") Integer limit, @Param("uid") Integer uid);

    public IPage<AnswerShow> getMyAnswers(Page<AnswerShow> page, @Param("id") Integer uid);

    public IPage<viewAnswers> getAll(Page<viewAnswers> page, @Param("qid") Integer qid);

    public IPage<answerAll> answersAll(Page<answerAll> page, @Param(Constants.WRAPPER) QueryWrapper<answerAll> wrapper);

}
