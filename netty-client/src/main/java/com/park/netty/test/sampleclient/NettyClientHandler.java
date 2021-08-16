package com.park.netty.test.sampleclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 서버로 문자열을 던지면 서버는 문자를 좀 더 붙여서 클라이언트로 던져준다.
 * <p>클라이언트는 서버로부터 문자열을 받아 channelRead0() 메소드를 호출
 * <p>받은 문자열을 화면에 출력
 */
public class NettyClientHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // 받은 문자열을 화면에 출력
        System.out.println((String)msg);
    }

    /**
     * exceptionCaught() 메소드는 예외가 발생했을 때 호출
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}