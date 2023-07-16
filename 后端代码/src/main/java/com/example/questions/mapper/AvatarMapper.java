package com.example.questions.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.example.questions.entity.Avatar;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author oliver
 * @since 2023-06-29
 */
@Mapper
public interface AvatarMapper extends BaseMapper<Avatar> {
    //修改用户头像的更新信息
    public int updateAvatar(@Param("avatar") Avatar avatar);
}
