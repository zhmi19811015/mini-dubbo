package com.test.api.impl;

import com.test.api.TestService;
import com.test.register.RpcService;

/**
 * @Author zhangming
 * @Date 2018/9/5 22:33
 **/
@RpcService(TestService.class)
public class TestServiceImpl implements TestService {
    @Override
    public String hello(String name) {
        return "Hello:"+name;
    }
}
