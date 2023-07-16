package com.example.questions.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.QuestionShow;
import com.example.questions.entity.recordsQ;
import com.example.questions.entity.Questions;
import com.baomidou.mybatisplus.extension.service.IService;

import java.sql.Wrapper;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author oliver
 * @since 2023-06-28
 */
public interface QuestionsService extends IService<Questions> {
    //获取最新的xx条记录
    public List<recordsQ> getRecords(Integer limit, Integer uid);

    //自定义分页，查询提问者uid的相关信息
    public IPage<QuestionShow> queryInfo(Page<QuestionShow> page, QueryWrapper<QuestionShow> wrapper);
}
