package com.test.register.impl;

import com.test.register.IServiceDiscovery;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhangming
 * @Date 2018/9/7 21:02
 **/

public class IServiceDiscoveryImpl implements IServiceDiscovery {
    private List<String> list = new ArrayList<>();
    private CuratorFramework curatorFramework;

    public  IServiceDiscoveryImpl(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder().connectString(ZkConfig.ZK_ADDRESS).
                retryPolicy(retryPolicy).sessionTimeoutMs(1000 * 6).connectionTimeoutMs(1000 * 6).build();
        curatorFramework.start();
    }

    @Override
    public String discovery(String serverName) {
        String path = ZkConfig.ZK_REGISTER_PATH+"/"+serverName;

        try{
            list =  curatorFramework.getChildren().forPath(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        //服务器端有多个IP时，list有多个值。
        return list.get(0);
    }
}
