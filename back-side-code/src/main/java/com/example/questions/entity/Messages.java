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
 * @since 2023-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Messages对象", description="")
public class Messages implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "提问者id，消息接收者")
    private Integer uid;

    @ApiModelProperty(value = "消息内容")
    private String message;

    @ApiModelProperty(value = "发送时间")
    private String time;

    @ApiModelProperty(value = "发送日期")
    private String date;

    @ApiModelProperty(value = "是否已读")
    private Integer flag;
}
