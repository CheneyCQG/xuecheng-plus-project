package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseParamsDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.exception.CommonError;
import com.xuecheng.exception.GlobalException;
import com.xuecheng.model.dto.PageParams;
import com.xuecheng.model.dto.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {
    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;
    @Autowired
    private CourseTeacherMapper courseTeacherMapper;
    @Override
    public PageResult<CourseBase> listPage(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        //分页查询
        IPage page = new Page(pageParams.getPageNo(), pageParams.getPageSize());
//        QueryWrapper<CourseBase> queryWrapper = new QueryWrapper<>();
//        if (!StringUtils.isEmpty(queryCourseParamsDto.getAuditStatus()))
//            queryWrapper.eq("audit_status",queryCourseParamsDto.getAuditStatus());
//        if (!StringUtils.isEmpty(queryCourseParamsDto.getCourseName()))
//            queryWrapper.like("name",queryCourseParamsDto.getCourseName());
//        if (!StringUtils.isEmpty(queryCourseParamsDto.getPublishStatus()))
//            queryWrapper.eq("status",queryCourseParamsDto.getPublishStatus());
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(
                !StringUtils.isEmpty(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus,
                queryCourseParamsDto.getAuditStatus());
        queryWrapper.like(
                !StringUtils.isEmpty(queryCourseParamsDto.getCourseName()),
                CourseBase::getName,
                queryCourseParamsDto.getCourseName());
        queryWrapper.eq(
                !StringUtils.isEmpty(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus,
                queryCourseParamsDto.getPublishStatus());
        IPage<CourseBase> ipage = courseBaseMapper.selectPage(page, queryWrapper);
        //封装成前端的格式
        PageResult<CourseBase> pageResultBean = new PageResult<>();
        pageResultBean.setItems(ipage.getRecords());
        pageResultBean.setCounts(ipage.getTotal());
        pageResultBean.setPage(ipage.getCurrent());
        pageResultBean.setPageSize(ipage.getSize());
        return pageResultBean;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto course(AddCourseParamsDto addCourseParamsDto) {
        //0合法性判断
        if (addCourseParamsDto.getOriginalPrice() < 0) {
            GlobalException.cast(CommonError.BALANCE_NOT_NAVIGATE_ERROR);
        }
        if (addCourseParamsDto.getPrice() < 0) {
            GlobalException.cast(CommonError.BALANCE_NOT_NAVIGATE_ERROR);
        }
        CourseBase courseBase = new CourseBase();
        CourseMarket courseMarket = new CourseMarket();
        if (addCourseParamsDto.getId() == null){
            //1先封装CourseBase对象

            BeanUtils.copyProperties(addCourseParamsDto,courseBase);

            courseBase.setStatus("203001");//代表未发布
            courseBase.setAuditStatus("202002");//未提交
            courseBase.setCreatePeople("jack");
            courseBase.setChangePeople("jack");
            courseBase.setCompanyId(12345L);
            courseBase.setCompanyName("东方瑞通");
            courseBase.setCreateDate(LocalDateTime.now());
            courseBase.setChangeDate(LocalDateTime.now());

            courseBaseMapper.insert(courseBase);


            BeanUtils.copyProperties(addCourseParamsDto,courseMarket);
            courseMarket.setId(courseBase.getId());

            courseMarketMapper.insert(courseMarket);


        }else {
            //1更新基本信息表

            BeanUtils.copyProperties(addCourseParamsDto,courseBase);
            courseBase.setChangeDate(LocalDateTime.now());
            courseBase.setChangePeople("rose");

            courseBaseMapper.updateById(courseBase);

            //2更新营销信息表

            BeanUtils.copyProperties(addCourseParamsDto,courseMarket);

            courseMarketMapper.updateById(courseMarket);
        }
        //3封装返回结果
        CourseBase courseBase1 = courseBaseMapper.selectById(courseBase.getId());
        CourseMarket courseMarket1 = courseMarketMapper.selectById(courseMarket.getId());

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase1,courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket1,courseBaseInfoDto);

        //4查询分类的名字
        CourseCategory firstCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        CourseCategory secondCategory = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        courseBaseInfoDto.setMtName(firstCategory.getName());
        courseBaseInfoDto.setStName(secondCategory.getName());
        return courseBaseInfoDto;

    }

    @Override
    public CourseBaseInfoDto course(String id) {
        //1基本课程信息表
        CourseBase courseBase = courseBaseMapper.selectById(id);
        //2营销表
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        CourseBaseInfoDto dto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,dto);
        BeanUtils.copyProperties(courseMarket,dto);
        return dto;
    }

    @Override
    @Transactional
    public void deleteCourseById(String id) {
        courseBaseMapper.deleteById(id);
        courseMarketMapper.deleteById(id);
        teachplanMapper.delete(new QueryWrapper<Teachplan>().eq("course_id", id));
        teachplanMediaMapper.delete(new QueryWrapper<TeachplanMedia>().eq("course_id", id));
        courseTeacherMapper.delete(new QueryWrapper<CourseTeacher>().eq("course_id", id));
    }
}
