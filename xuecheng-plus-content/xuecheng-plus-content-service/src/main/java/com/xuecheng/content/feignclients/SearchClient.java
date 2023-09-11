package com.xuecheng.content.feignclients;

import com.xuecheng.content.model.dto.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("search")
public interface SearchClient {
    @PostMapping("search/index/course")
    public Boolean add(@RequestBody CourseIndex courseIndex);
}
