package com.test.register;

import com.test.api.TestService;
import com.test.proxy.RpcClientProxy;
import com.test.register.impl.IServiceDiscoveryImpl;
import com.test.register.impl.ZkConfig;

/**
 * @Author zhangming
 * @Date 2018/9/7 21:17
 **/
public class ClientTest {
    public static void main(String[] args) {
        IServiceDiscovery iServiceDiscovery = new IServiceDiscoveryImpl();

        //netty 通信
        //注意 客户端实际在调用服务器的方法时 是无感知的。感觉就像调用本地方法
        //动态代理
        RpcClientProxy rpcClientProxy = new RpcClientProxy(iServiceDiscovery);
        TestService testService = rpcClientProxy.create(TestService.class);
        String str = testService.hello("zhangming");
        System.out.println(str);

    }
}
