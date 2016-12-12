package com.primary;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.primary.domain.Request;
import com.primary.domain.Response;
import com.primary.handlers.HttpUrlAwareServerHandlerInitializer;
import com.primary.suppliers.HelloWorldSupplier;
import com.primary.suppliers.NotFoundSupplier;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Main
{
	static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

	public static void main(String... args) throws InterruptedException
	{

		/**
		 * First approach to resolve functions - functions works as controllers.
		 */
		final Map<String, Function<Request, Response>> routes = //
				ImmutableMap.of("/", new HelloWorldSupplier(),
							"/404",new NotFoundSupplier());

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(2);

		try
		{
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup) //
					.channel(NioServerSocketChannel.class)//
					.handler(new LoggingHandler(LogLevel.DEBUG)) //
					.childHandler(new HttpUrlAwareServerHandlerInitializer(routes)); //

			Channel channel = serverBootstrap.bind(PORT).sync().channel();

			System.err.println("Open your web browser and navigate to " +
					"http://127.0.0.1:" + PORT + '/');

			channel.closeFuture().sync();
		}
		finally
		{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
