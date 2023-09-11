package com.xuecheng.content.jobhandler;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclients.MediaClient;
import com.xuecheng.content.feignclients.SearchClient;
import com.xuecheng.content.model.dto.CourseIndex;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CourseBaseService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.exception.GlobalException;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

@Component
public class CoursePublishJobHandler extends MessageProcessAbstract {
    @Autowired
    private MqMessageService mqMessageService;
    @Autowired
    private CoursePublishService coursePublishService;
    @Autowired
    private Configuration configuration;

    @Autowired
    private MediaClient mediaClient;
    @Autowired
    private SearchClient searchClient;
    @Autowired
    private CourseBaseService courseBaseService;


    @XxlJob("handlerCoursePublishJob")
    public void handlerCoursePublish(){
        System.out.println("正在处理发布的课程");
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        process(shardIndex,shardTotal,"course_publish",30,60);
    }
    //处理我们这个任务需要执行的操作
    @Override
    public boolean execute(MqMessage mqMessage) {
        System.out.println("正在处理某个消息"+mqMessage.getId()+"业务建"+mqMessage.getBusinessKey1());

        //1写redis缓存
//        saveToRedis(mqMessage);

        //2写es
        saveToEs(mqMessage);

        //3写静态化页面到minio
        saveToMinio(mqMessage);

        return false;
    }

    private void saveToMinio(MqMessage mqMessage) {
        System.out.println("上传到minio");
        if (mqMessageService.getStageThree(mqMessage.getId()) == 1) {
            System.out.println("minio不需要再写了");
        }
        //把课程预览的模板页和相关的数据静态化后上传到minio
        //把课程预览的模板页和相关数据静态化后，上传到minio
        try {
            //静态化
            Template template = configuration.getTemplate("course_template.ftl");
            CoursePreviewDto previewDto = coursePublishService.findAllCourseInfoById(mqMessage.getBusinessKey1());
            HashMap<String,CoursePreviewDto> map = new HashMap<String, CoursePreviewDto>();
            map.put("model",previewDto);
            File file = new File(mqMessage.getBusinessKey1() + ".html");
            template.process(map,new FileWriter(file));
            //上传到minio
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            mediaClient.coursefile(multipartFile,"course/"+file.getName());

        } catch (Exception e) {
            e.printStackTrace();
            GlobalException.cast("页面静态化失败");
        }
        //修改状态
//        mqMessageService.completedStageThree(mqMessage.getId());
    }

    private void saveToEs(MqMessage mqMessage) {
        if (mqMessageService.getStageTwo(mqMessage.getId()) == 1) {
            System.out.println("es不需要再写了");
        }
        //写es
        CoursePublish coursePublish = coursePublishService.getById(mqMessage.getBusinessKey1());
        if (coursePublish == null){
            GlobalException.cast("该课程未发布");
        }
        //1复制基本信息
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        //2解析market信息
        CourseMarket courseMarket = JSON.parseObject(coursePublish.getMarket(), CourseMarket.class);
        BeanUtils.copyProperties(courseMarket,courseIndex);
        Boolean result = searchClient.add(courseIndex);
        if (!result){
            GlobalException.cast("添加索引库失败");
        }
        //修改状态
//        mqMessageService.completedStageTwo(mqMessage.getId());
    }

    private void saveToRedis(MqMessage mqMessage) {
        if (mqMessageService.getStageOne(mqMessage.getId()) == 1) {
            System.out.println("redis不需要再写了");
        }

        //写redis

        //修改状态
        mqMessageService.completedStageOne(mqMessage.getId());
    }
}
