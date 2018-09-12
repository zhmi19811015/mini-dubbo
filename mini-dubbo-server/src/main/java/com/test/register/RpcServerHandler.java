package com.test.register;

import com.test.bean.RpcRequest;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhangming
 * @Date 2018/9/7 22:26 ChannelInboundHandlerAdapter
 **/
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    private Map<String,Object> handlerMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> handlerMap) {
        System.out.println("RpcServerHandler构造函数：");
        this.handlerMap = handlerMap;
    }

    //处理得到客户端的请求数据，然后根据这个请求数据进行操作
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("接受的客户端信息："+msg);
        //msg 客户端数据
        //ctx 向客户端写数据
        RpcRequest rpcRequest = (RpcRequest)msg;
        Object result = new Object();
        //根据这个reques调用server的对应的方法
       if(handlerMap.containsKey(rpcRequest.getClassName())){
            //执行服务器对应对象
           Object clazz = handlerMap.get(rpcRequest.getClassName());
           Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypes());
           //反射执行
           result =  method.invoke(clazz,rpcRequest.getParams());
       }
       ctx.write(result);
        System.out.println("返回客户端信息："+result);
       ctx.flush();
       ctx.close();

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String remoteAddress = ctx.channel().remoteAddress().toString();
        System.out.println("客户端IP：" + remoteAddress);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive" );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        System.out.println("exceptionCaught" );
        ctx.close();
    }
}
