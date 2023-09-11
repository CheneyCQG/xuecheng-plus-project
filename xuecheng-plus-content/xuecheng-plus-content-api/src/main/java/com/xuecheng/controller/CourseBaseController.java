package com.xuecheng.controller;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xuecheng.content.model.dto.AddCourseParamsDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.exception.InvalidationGroups;
import com.xuecheng.model.dto.PageParams;
import com.xuecheng.model.dto.PageResult;
import com.xuecheng.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息 前端控制器
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@RestController
@Api
public class CourseBaseController {
    @Autowired
    private CourseBaseService courseBaseService;
    @RequestMapping("/course/list")
    @ApiOperation(value = "分页查询")
    public PageResult<CourseBase> list(@ApiParam(value = "分页参数") PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        return courseBaseService.listPage(pageParams,queryCourseParamsDto);
    }
    @RequestMapping("/course")
    public CourseBaseInfoDto course(@RequestBody @Validated({InvalidationGroups.INSERT.class}) AddCourseParamsDto addCourseParamsDto){
        return courseBaseService.course(addCourseParamsDto);
    }

    @RequestMapping("/course/{id}")
    public CourseBaseInfoDto course(@PathVariable("id") String id){
        //获取用户信息
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        return courseBaseService.course(id);
    }


    @DeleteMapping("/course/{id}")
    public void deleteCourseById(@PathVariable("id") String id){
        courseBaseService.deleteCourseById(id);
    }


}

@Data
@TableName("xc_user")
class XcUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String password;

    private String salt;

    private String name;
    private String nickname;
    private String wxUnionid;
    private String companyId;
    /**
     * 头像
     */
    private String userpic;

    private String utype;

    private LocalDateTime birthday;

    private String sex;

    private String email;

    private String cellphone;

    private String qq;

    /**
     * 用户状态
     */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
