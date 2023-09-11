package com.xuecheng.auth.service.impl;

import com.xuecheng.auth.service.AuthService;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import org.springframework.stereotype.Service;

@Service("sms_auth")
public class SmsAuthServiceImpl implements AuthService {
    @Override
    public XcUser vertify(AuthParamsDto authParamsDto) {
        //获取手机号  从数据库获取验证码

        //比较

        //从数据库根据手机号查询返回
        return null;
    }
}
