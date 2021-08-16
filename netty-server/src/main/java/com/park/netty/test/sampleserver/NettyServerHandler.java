package com.park.netty.test.sampleserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 클라이언트로부터 메시지를 받았을 때, 처리할 Handler 클래스다
 * <p>결국 클라이언트에서 메시지가 날라오면 실행되는 메소드
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 클라이언트에서 메시지가 날라오면 실행되는 메소드
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // super.channelRead(ctx, msg);

        // 문자열을 전달받아서 채널에 "Response : " 문자열과 "' received\n" 문자열을 앞뒤에 붙여서 다시 전달
        String message = (String)msg;
        log.info("===========================================");
        log.info(message);
        log.info("===========================================");

        Channel channel = ctx.channel();
        channel.writeAndFlush("Response : '" + message + "' received\n");

        if ("quit".equals(message)) {
            ctx.close();
        }
    }
}
