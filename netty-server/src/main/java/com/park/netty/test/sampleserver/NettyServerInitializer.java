package com.park.netty.test.sampleserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 서버측 채널 파이프라인을 구성하는 Initializer 클래스
 * <p>TCP 연결이 accept 되었을 때 실행
 * <p>파이프라인을 생성
 * <p>이벤트(메시지)가 발생하면 소켓 채널에서 읽어 들이는 것인지 소켓 채널로 쓰는 것인지에 따라서 파이프라인의 핸들러가 수행
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 여기서는 new를 통해 매번 객체를 생성해서 파이프라인을 구축했지만 ReadOnly인 경우 등에는 하나의 객체를 여러 채널이 공유해서 쓰는 것도 가능

        // LineBasedFrameDecoder()를 통해 네트워크에서 전송되는 바이트 값을 읽어 라인 문자열로 변환
        pipeline.addLast(new LineBasedFrameDecoder(65536));


        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        // 필요하다면 디코딩을 한 다음 EchoServerHandler를 호출
        // 이후 write()가 되면 StringEncoder()를 통해 네트워크 너머로 데이터를 전송
        pipeline.addLast(new NettyServerHandler());
        
    }
    
}
