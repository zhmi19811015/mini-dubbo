package com.test.proxy;

import com.test.bean.RpcRequest;
import com.test.register.IServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author zhangming
 * @Date 2018/9/7 22:45
 **/
public class RpcClientProxy {
    private IServiceDiscovery iServiceDiscovery;

    public RpcClientProxy(IServiceDiscovery iServiceDiscovery) {
        this.iServiceDiscovery = iServiceDiscovery;
    }

    /**
     * 动态代理
     * @param interfaceClass
     * @param <T>
     * @return 具体实现类对象
     */
    public <T> T create(Class<T> interfaceClass){

        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //代理对象要做的事情.
                //netty数据交换
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setClassName(method.getDeclaringClass().getName());
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setTypes(method.getParameterTypes());
                rpcRequest.setParams(args);

                //服务发现
                String serviceName = interfaceClass.getName();
                String serviceAddress = iServiceDiscovery.discovery(serviceName);
                String[] addrs = serviceAddress.split(":");
                String ip = addrs[0];
                int port = Integer.parseInt(addrs[1]);

                //netty 连接 进行交互
                RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
                EventLoopGroup boosGroup = new NioEventLoopGroup();
                try{
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(boosGroup);
                    bootstrap.channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true).handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //业务代码
                            ChannelPipeline pipeline = socketChannel.pipeline();
//                        //关注handler
//                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4));
//                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            //业务handler
                            pipeline.addLast(rpcProxyHandler);
                        }
                    });
                    //连接服务器
                    ChannelFuture future = bootstrap.connect(ip,port).sync();
                    //将封装的对象写
                    future.channel().writeAndFlush(rpcRequest);
                    future.channel().closeFuture().sync();

                    return rpcProxyHandler.getResponese();
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    boosGroup.shutdownGracefully();
                    System.out.println("客户端优雅的释放了线程资源...");
                }
                return null;

            }
        });
    }
}
