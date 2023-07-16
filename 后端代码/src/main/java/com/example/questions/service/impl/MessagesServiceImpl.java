package com.example.questions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.common.Result;
import com.example.questions.entity.Messages;
import com.example.questions.entity.messageShow;
import com.example.questions.mapper.MessagesMapper;
import com.example.questions.service.MessagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author oliver
 * @since 2023-07-08
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesMapper, Messages> implements MessagesService {
    @Autowired
    private MessagesMapper messagesMapper;

    @Override
    public IPage<messageShow> allMessages(Page<messageShow> page, QueryWrapper<messageShow> wrapper){
        return messagesMapper.allMessages(page, wrapper);
    }
}
