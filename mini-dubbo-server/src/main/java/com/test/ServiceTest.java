package com.test;

import com.test.api.TestService;
import com.test.api.impl.TestServiceImpl;
import com.test.register.IServiceRegister;
import com.test.register.RpcServer;
import com.test.register.IServiceRegisterImpl;

import java.io.IOException;

/**
 * @Author zhangming
 * @Date 2018/9/5 23:01
 **/
public class ServiceTest {


    public static void main(String[] args) {
        //将testService注册到zk
        TestService testService = new TestServiceImpl();
        IServiceRegister serviceRegister = new IServiceRegisterImpl();

        //serviceRegister.register("com.test.api.hello","127.0.0.1");
        String serverAddress = "127.0.0.1:8080";
        RpcServer rpcServer = new RpcServer(serviceRegister,serverAddress);
        rpcServer.bind(testService);
        rpcServer.registerAndListen();

    }
}
