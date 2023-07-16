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
@ApiModel(value="Avatar对象", description="")
public class Avatar implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "头像路径")
    private String avatar;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;


}
