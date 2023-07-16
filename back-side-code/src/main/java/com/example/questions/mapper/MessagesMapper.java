package com.example.questions.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.Messages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.questions.entity.messageShow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author oliver
 * @since 2023-07-08
 */
@Mapper
public interface MessagesMapper extends BaseMapper<Messages> {
    public IPage<messageShow> allMessages(Page<messageShow> page, @Param(Constants.WRAPPER) QueryWrapper<messageShow> wrapper);
}
