package com.example.questions.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2023-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Menu对象", description="")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "编码")
    @TableField("menuCode")
    private String menucode;

    @ApiModelProperty(value = "菜单选项名称")
    @TableField("menuName")
    private String menuname;

    @ApiModelProperty(value = "菜单导航路由名称")
    @TableField("menuRoute")
    private String menuroute;

    @ApiModelProperty(value = "权限：0-超级管理员 1-管理员 2-普通用户，可用逗号组合使用")
    @TableField("menuRight")
    private String menuright;

    @ApiModelProperty(value = "组件路径")
    @TableField("menuComponentPath")
    private String menucomponentpath;

    @ApiModelProperty(value = "菜单图标")
    @TableField("menuIcon")
    private String menuicon;


}
