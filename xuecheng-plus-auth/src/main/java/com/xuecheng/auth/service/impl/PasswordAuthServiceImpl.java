package com.xuecheng.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("password_auth")
public class PasswordAuthServiceImpl implements AuthService {
    @Autowired
    private XcUserMapper xcUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public XcUser vertify(AuthParamsDto authParamsDto) {
        //根据用户名查询数据库
        //数据库查寻用户
        QueryWrapper<XcUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",authParamsDto.getUsername());
        XcUser xcUser = xcUserMapper.selectOne(queryWrapper);
        //如果没有查询到用户信息，我们直接返回null即可，DaoAuthticationProvider会自动抛出异常
        if (xcUser == null) {
            return null;
        }
        //校验
        if (!passwordEncoder.matches(authParamsDto.getPassword(),xcUser.getPassword())) {
            throw new RuntimeException("用户名或者密码错误");
        }
        return xcUser;
        //比较登录的密码和数据库查询出来的对象

    }
}
