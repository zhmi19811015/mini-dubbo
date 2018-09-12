package com.test.proxy;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author zhangming
 * @Date 2018/9/7 23:34 ChannelInboundHandlerAdapter
 **/
public class RpcProxyHandler extends ChannelDuplexHandler {
    //服务端返回的结果
    private Object responese;

    public Object getResponese() {
        return responese;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //msg服务端数据
        responese = msg;
    }
}
