package com.example.questions.service.impl;

import com.example.questions.entity.Avatar;
import com.example.questions.mapper.AvatarMapper;
import com.example.questions.service.AvatarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author oliver
 * @since 2023-06-29
 */
@Service
public class AvatarServiceImpl extends ServiceImpl<AvatarMapper, Avatar> implements AvatarService {
    @Resource
    private AvatarMapper avatarMapper;

    @Override
    public int updateAvatar(Avatar avatar){
        return avatarMapper.updateAvatar(avatar);
    }
}
