package com.example.questions.service;

import com.example.questions.entity.Avatar;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author oliver
 * @since 2023-06-29
 */
public interface AvatarService extends IService<Avatar> {
    public int updateAvatar(Avatar avatar);
}
