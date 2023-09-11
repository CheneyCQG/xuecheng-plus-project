package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MySqlUserDetailService implements UserDetailsService {
  
    
    @Autowired
    private ApplicationContext applicationContext;
    @Override
    public UserDetails loadUserByUsername(String authParamDtoJson) throws UsernameNotFoundException {
        //把Json串转为Json对象
        AuthParamsDto authParamsDto = JSON.parseObject(authParamDtoJson, AuthParamsDto.class);
        AuthService authService = applicationContext.getBean(authParamsDto.getAuthType() + "_auth", AuthService.class);
        if (authService == null) {
            return null;
        }
        XcUser xcUser = authService.vertify(authParamsDto);
        if (xcUser == null)
            return null;
        UserDetails userDetails = getUserDetails(xcUser);
        return userDetails;

    }

    private UserDetails getUserDetails(XcUser xcUser) {
        //如果查到了封装未userdetails
        String password = xcUser.getPassword();
        xcUser.setPassword("");
        String userJson = JSON.toJSONString(xcUser);
        return User.withUsername(userJson).password(password).authorities("p1").build();
    }
}
