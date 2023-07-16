package com.example.questions.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author oliver
 * @since 2023-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="User对象", description="")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id，主键")
    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;

    @ApiModelProperty(value = "账号（唯一，不可更改）")
    private String account;

    @ApiModelProperty(value = "用户名（可更改，可重复）")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "性别，0：男，1：女")
    private String gender;


    @ApiModelProperty(value = "用户权限，0：普通用户，1：管理员")
    private Integer permission;

    @ApiModelProperty(value = "上次登录时间")
    private String loginTime;

    @ApiModelProperty(value = "注册时间")
    private String registerTime;

}
