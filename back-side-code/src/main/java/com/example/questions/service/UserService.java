package com.example.questions.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.questions.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.sql.Wrapper;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author oliver
 * @since 2023-06-29
 */
public interface UserService extends IService<User> {
    // 查询用户和头像表的联合
    public IPage<User> userShow(Page<User> page, QueryWrapper wrapper);
}
