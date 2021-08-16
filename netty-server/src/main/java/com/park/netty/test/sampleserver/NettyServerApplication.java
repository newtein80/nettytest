package com.park.netty.test.sampleserver;

import java.net.InetSocketAddress;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Netty Server 생성
 * <p>Netty TCP Server를 생성하기 위해서는 다음 과정을 수행해야한다.
 * <p>1. EventLoopGroup을 생성
 * <p>2. Bootstrap 생성 및 설정
 * <p>3. ChannelInitializer 생성
 * <p>4. 서버 시작
 */
@Slf4j
// @SpringBootApplication
public class NettyServerApplication { // extends SpringBootServletInitializer {

	private static final int SERVER_PORT = 11011;

	private final ChannelGroup allChannels = new DefaultChannelGroup("server", GlobalEventExecutor.INSTANCE);

	/**
	 * NIO 기반의 EventLoop를 생성
	 * <p>Boss Thread는 ServerSocket을 Listen
	 * <p>여기서 만들어진 Channel에서 넘어온 데이터는 workerEventLoopGroup에서 처리
	 */
	private EventLoopGroup bossEventLoopGroup;

	/**
	 * Worker Thread는 만들어진 Channel에서 넘어온 이벤트를 처리
	 */
	private EventLoopGroup workEventLoopGroup;

	public void startServer() {
		bossEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
		workEventLoopGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("work"));

		// netty 서버를 생성하기 위한 헬퍼 클래스인 ServerBootstrap 인스턴스를 생성
		ServerBootstrap serverBootstrap = new ServerBootstrap();

		// EventLoopGroup을 bootstrap의 group() 메소드로 세팅
		serverBootstrap.group(bossEventLoopGroup, workEventLoopGroup);

		// Channel 생성시 사용할 클래스 (NIO 소켓을 이용한 채널)
		// - 채널을 생성할 때 NIO 소켓을 이용한 채널을 생성하도록 channel() 메소드에 NioServerSocketChannel.class를 인자로 넘겨준다.
		serverBootstrap.channel(NioServerSocketChannel.class);

		// accept 되어 생성되는 TCP Channel 설정
		serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
		serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

		// Client Request를 처리할 Handler 등록
		// 채널 파이프라인을 설정하기 위해 EchoServerInitializer 객체를 할당
		// 서버 소켓에 연결이 들어오면 이 객체가 호출되어 소켓 채널을 초기화
		serverBootstrap.childHandler(new NettyServerInitializer());

		try {
			// Channel 생성후 기다림
			// bind() 메소드로 서버 소켓에 포트를 바인딩
			// sync() 메소드를 호출해서 바인딩이 완료될 때까지 기다린다. 이 코드가 지나가면 서버가 시작된다.
			ChannelFuture bindFuture = serverBootstrap.bind(new InetSocketAddress(SERVER_PORT)).sync();
			Channel channel = bindFuture.channel();
			allChannels.add(channel);

			// Channel이 닫힐 때까지 대기
			bindFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			throw new RuntimeException();
		} finally {
			close();
		}
	}

	private void close() {
		allChannels.close().awaitUninterruptibly();
		workEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
		bossEventLoopGroup.shutdownGracefully().awaitUninterruptibly();
	}

	public static void main(String[] args) {
		// SpringApplication.run(NettyServerApplication.class, args);
		log.info("Netty Server App Start!");
		new NettyServerApplication().startServer();
	}
	
	// @Override
	// protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
	// 	return builder.sources(NettyServerApplication.class);
	// }
	
	@Bean
	public CommandLineRunner runner() {
		// log.info("Netty Server App Start!");
		// startServer();
	    return (a) -> {
	    	log.info("========================================================================");
	    };
	};

}
