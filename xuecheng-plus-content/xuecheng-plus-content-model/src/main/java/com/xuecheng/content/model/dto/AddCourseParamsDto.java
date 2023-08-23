package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseBase;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AddCourseParamsDto extends CourseBase {

    /**
     * 收费规则，对应数据字典
     */
    @NotNull
    private String charge;

    /**
     * 现价
     */
    @Min(value = 0L)
    private Float price;

    /**
     * 原价
     */
    @Min(value = 0L)
    private Float originalPrice;

    /**
     * 咨询qq
     */

    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    @Min(value = 10L)
    private Integer validDays;

}
