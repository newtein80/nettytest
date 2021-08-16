package com.park.netty.test.sampleclient;

import java.net.InetSocketAddress;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Netty Client 생성
 * <p>Netty TCP Client를 생성하기 위해서는 다음 과정을 수행해야한다.
 * <p>Client 생성도 서버와 비슷하다.
 * <p>1. EventLoopGroup을 생성
 * <p>2. Bootstrap 생성 및 설정
 * <p>3. ChannelInitializer 생성
 * <p>4. 클라이언트 시작
 */
@Slf4j
// @SpringBootApplication
public class NettyClientApplication { // extends SpringBootServletInitializer {

	private static final int SERVER_PORT = 11011;
    private final String host;
    private final int port;

    private Channel serverChannel;

    /**
     * EventLoopGroup 생성
     * <p>서버와 마찬가지로 NIO를 사용하기 위해 EventLoopGroup을 생성
     * <p>다른 점은 클라이언트라서 서버소켓에 listen하기 위한 boss 그룹은 없다는 점
     */
    private EventLoopGroup eventLoopGroup;

    public NettyClientApplication(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        eventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("client"));

        /**
         * Bootstrap 생성 및 설정
         */
        Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup);

        /**
         * 클라이언트 생성을 위해서 마찬가지로 bootstrap 설정
         */
        bootstrap.channel(NioSocketChannel.class);
        /**
         * remoteAddress() 메소드로 접속할 서버 소켓의 주소와 포트를 입력
         */
        bootstrap.remoteAddress(new InetSocketAddress(host, port));
        bootstrap.handler(new NettyClientInitializer());

        /**
         * connect() 메소드로 서버 소켓에 연결을 하고 sync() 메소드로 기다린다.
         * Client 시작
         * 종료시에는 channelFuture.channel().closeFuture().sync();
         */
        serverChannel = bootstrap.connect().sync().channel();
    }

    private void start() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        String message;
        ChannelFuture future;

        while(true) {
            // 사용자 입력
            message = scanner.nextLine();

            // Server로 전송
            future = serverChannel.writeAndFlush(message.concat("\n"));

            if("quit".equals(message)){
                /**
                 * Client 종료
                 */
                serverChannel.closeFuture().sync();
                break;
            }
        }

        // 종료되기 전 모든 메시지가 flush 될때까지 기다림
        if(future != null){
            future.sync();
        }
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

	public static void main(String[] args) throws Exception {
		// SpringApplication.run(NettyClientApplication.class, args);
		// log.info("Netty Client App Start!");
		NettyClientApplication client = new NettyClientApplication("127.0.0.1", SERVER_PORT);

        try {
            client.connect();
            client.start();
        } finally {
            client.close();
        }
	}
	
	// @Override
	// protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	// 	return builder.sources(NettyClientApplication.class);
	// }
	
	@Bean
	public CommandLineRunner runner() throws Exception {
		// log.info("Netty Client App Start!");
		// NettyClientApplication client = new NettyClientApplication("127.0.0.1", SERVER_PORT);

        // try {
        //     client.connect();
        //     client.start();
        // } finally {
        //     client.close();
        // }
	    return (a) -> {
	    	log.info("========================================================================");
	    };
	};

}
