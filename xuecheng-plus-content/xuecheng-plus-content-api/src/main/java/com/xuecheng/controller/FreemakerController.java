package com.xuecheng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FreemakerController {
    @RequestMapping("test")
    public String test(Model model){
        model.addAttribute("name","jack");
        return "test";
    }
    @RequestMapping("test1")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("test");
        modelAndView.addObject("name","rose");
        return modelAndView;
    }
}
