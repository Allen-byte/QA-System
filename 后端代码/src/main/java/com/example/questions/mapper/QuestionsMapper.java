package com.example.questions.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.AnswerShow;
import com.example.questions.entity.QuestionShow;
import com.example.questions.entity.recordsQ;
import com.example.questions.entity.Questions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
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
 * @since 2023-06-28
 */
@Mapper
public interface QuestionsMapper extends BaseMapper<Questions> {
    public List<recordsQ> getRecords(@Param("limit") Integer limit, @Param("uid") Integer uid);

    public IPage<QuestionShow> queryInfo(Page<QuestionShow> page, @Param(Constants.WRAPPER) QueryWrapper<QuestionShow> wrapper);
}
