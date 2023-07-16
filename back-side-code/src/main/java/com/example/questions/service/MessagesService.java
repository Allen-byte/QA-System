package com.example.questions.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.common.Result;
import com.example.questions.entity.Messages;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.questions.entity.messageShow;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author oliver
 * @since 2023-07-08
 */
public interface MessagesService extends IService<Messages> {
    //管理端查询全部消息
    public IPage<messageShow> allMessages(Page<messageShow> page, QueryWrapper<messageShow> wrapper);
}
