package com.xuecheng.content.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.exception.CommonError;
import com.xuecheng.exception.GlobalException;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xuecheng.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author Cheney
 */
@Slf4j
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements CoursePublishService {
    @Autowired
    private CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseMarketMapper courseMarketMapper;
    @Autowired
    private TeachplanService teachplanService;
    @Autowired
    private CourseBaseService courseBaseService;
    @Autowired
    private CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    private CourseCategoryMapper courseCategoryMapper;
    @Autowired
    private CoursePublishMapper coursePublishMapper;
    @Autowired
    private MqMessageService mqMessageService;
    @Override
    public CoursePreviewDto findAllCourseInfoById(String courseId) {
        //查询基本和营销信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        //2查询课程大纲
        List<TeachplanDto> teachplanDto = teachplanService.teachplanTreeNodes(courseId);

        //3师资信息

        //4封装到CoursePreviewDto
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        coursePreviewDto.setCourseTeacher(new CourseTeacher());
        coursePreviewDto.setTeachplans(teachplanDto);


        return coursePreviewDto;
    }

    /**
     * 提交审核
     * @param id
     * @param courseId
     */
    @Transactional
    @Override
    public void commitAudit(Long id, Long courseId) {
        //1合法性判断
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            GlobalException.cast("该课程不存在！！");
        }
        if (!id.equals(courseBase.getCompanyId())) {
            GlobalException.cast("请求非法，不能提交其他机构课程");
        }
        if ("202003".equals(courseBase.getAuditStatus())) {
            GlobalException.cast("该课程正在审核中");
        }
        if (StringUtils.isEmpty(courseBase.getPic())) {
            GlobalException.cast("缺少课程封面图片");
        }
        List<TeachplanDto> teachplanDtos = teachplanService.teachplanTreeNodes(String.valueOf(courseId));
        if (teachplanDtos == null || teachplanDtos.size() == 0) {
            GlobalException.cast("缺少课程大纲");
        }
        //2查询所有课程数据，插入预发布表
        CourseMarket courseMarket = courseMarketMapper.selectById(String.valueOf(courseId));
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBase,coursePublishPre);
        JsonUtil.objectTojson(courseMarket);
        coursePublishPre.setMarket(JsonUtil.objectTojson(courseMarket));
        coursePublishPre.setTeachplan(JsonUtil.objectTojson(teachplanDtos));
        coursePublishPre.setStatus("202003");//已提交
        coursePublishPre.setTeachers(null);
        //查询分类名字
        CourseCategory courseCategory = courseCategoryMapper.selectById(coursePublishPre.getMt());
        coursePublishPre.setMtName(courseCategory.getName());
        CourseCategory courseCategory1 = courseCategoryMapper.selectById(coursePublishPre.getSt());
        coursePublishPre.setStName(courseCategory1.getName());
        coursePublishPreMapper.insert(coursePublishPre);
        //3修改基本信息状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }
    @Transactional
    @Override
    public void coursepublish(Long id, Long courseId) {
        //1判断
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            GlobalException.cast("课程未提交审核");
        }
        if (!id.equals(coursePublishPre.getCompanyId())) {
            GlobalException.cast("机构不对");
        }
        if (!"202004".equals(coursePublishPre.getStatus())) {
            GlobalException.cast("审核未通过");
        }
        //2向发布表中插入课程的信息
        //3更新课程基本信息表的发布状态
        savaCourseToPublish(coursePublishPre,courseId);

        //3向消息表中插入课程信息
        savaCourseMqMessage(courseId);

        //5从预发布表中删除信息
        coursePublishPreMapper.deleteById(courseId);
    }

    private void savaCourseToPublish(CoursePublishPre coursePublishPre, Long courseId) {
        //2向发布表中插入课程的信息
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setOnlineDate(LocalDateTime.now());
        coursePublish.setStatus("203002");
        coursePublishMapper.insert(coursePublish);
        //3更新课程基本信息表的发布状态

        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }

    private void savaCourseMqMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            GlobalException.cast(CommonError.COMMON_ERROR);
        }
    }
}
