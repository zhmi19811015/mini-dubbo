package com.test.register;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @Author zhangming
 * @Date 2018/9/5 23:05
 **/
public class IServiceRegisterImpl implements IServiceRegister {
    private CuratorFramework curatorFramework;

    public IServiceRegisterImpl(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder().connectString(ZkConfig.ZK_ADDRESS).
                retryPolicy(retryPolicy).sessionTimeoutMs(1000 * 6).connectionTimeoutMs(1000 * 6).build();
        curatorFramework.start();
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        String servicePath = ZkConfig.ZK_REGISTER_PATH+"/"+serviceName;

        try{
            //判断节点是否存在，不存在创建
            if (curatorFramework.checkExists().forPath(servicePath) == null){
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath,"0".getBytes());
            }
            System.out.println("serviceName创建成功："+servicePath);
            String addressPath = servicePath+"/"+serviceAddress;
            String addNode = "";
            if (curatorFramework.checkExists().forPath(addressPath) == null){
                addNode = curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath(addressPath,"0".getBytes());
            }
            System.out.println("serviceAddress创建成功："+addNode);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
