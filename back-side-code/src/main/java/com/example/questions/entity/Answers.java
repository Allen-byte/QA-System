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
 * @since 2023-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Answers对象", description="")
public class Answers implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "问题id")
    private Integer qid;

    @ApiModelProperty(value = "回答人id")
    private Integer uid;

    @ApiModelProperty(value = "回答内容")
    private String answer;

    @ApiModelProperty(value = "回答时间")
    private String answerTime;

    @ApiModelProperty(value = "回答日期")
    private String answerDate;

}
