package com.test.register;

/**
 * @Author zhangming
 * @Date 2018/9/5 23:03
 **/
public interface IServiceRegister {
    /**
     * 将serviceName与serviceAddress绑定
     * @param serviceName 服务名称
     * @param serviceAddress 服务器地址 192.168.12.123:8080
     */
    void register(String serviceName,String serviceAddress);
}
