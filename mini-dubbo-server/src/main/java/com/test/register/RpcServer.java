package com.test.register;

import com.sun.corba.se.impl.interceptors.PICurrent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangming
 * @Date 2018/9/7 21:29
 **/
public class RpcServer {
    //1、服务注册
    //2、服务监听  根据serverAddress进行监听
    private  IServiceRegister iServiceRegister;
    private String serverAddress;
    private Map<String,Object> handlerMap = new HashMap<>();

    public RpcServer(IServiceRegister iServiceRegister, String serverAddress) {
        this.iServiceRegister = iServiceRegister;
        this.serverAddress = serverAddress;
    }

    /**
     * 服务名称与服务对象绑定
     * @param services
     */
    public void bind(Object... services){
        for (Object service : services){
            RpcService annotation = service.getClass().getAnnotation(RpcService.class);
            String serviceName = annotation.value().getName();
            handlerMap.put(serviceName,service);

        }
    }

    /**
     * 服务注册及监听
     * @throws Exception
     */
    public void registerAndListen() {
        //注册服务器地址，先遍历服务名称
        for (String servcieName : handlerMap.keySet()){
            //注册服务名称和服务地址
            iServiceRegister.register(servcieName,serverAddress);
        }

            //监听端口、进行与客户端通讯 netty
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{  //启动netty
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup,workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    //业务代码
                    ChannelPipeline pipeline = channel.pipeline();
//                    //关注handler
//                    pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4));
//                    pipeline.addLast(new LengthFieldPrepender(4));
                    pipeline.addLast(new ObjectEncoder());
                    pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                    //业务handler
                    pipeline.addLast(new RpcServerHandler(handlerMap));
                }
            }).option(ChannelOption.SO_BACKLOG,1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            //netty 端口
            String[] addrs = serverAddress.split(":");
            String ip = addrs[0];
            int port = Integer.parseInt(addrs[1]);
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("netty服务器启动成功，等待客户端的连接");
            future.channel().closeFuture().sync();
            System.out.println("netty-====");
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }


    }
}
