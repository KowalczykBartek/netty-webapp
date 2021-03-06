package com.primary;

import static com.google.common.collect.ImmutableMap.of;
import static io.netty.handler.codec.http.HttpMethod.*;

import java.util.Map;

import com.primary.cassandra.CassandraQueryService;
import com.primary.domain.Route;
import com.primary.handlers.business.GetEntityHandler;
import com.primary.handlers.business.InsertEntityHandler;
import com.primary.handlers.HttpUrlAwareServerHandlerInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Main {
    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
    static final String CASSANDRA_CONTACT_POINT = System.getProperty("cassandra_contact_point", "127.0.0.1");

    public static void main(final String... args) throws InterruptedException {
        final CassandraQueryService queryService = CassandraQueryService.build(CASSANDRA_CONTACT_POINT);

        System.out.println("Cluster initialized");

        final Map<Route, ChannelHandler> routes = //
                of(Route.just("/", PUT), new InsertEntityHandler(queryService),
                        Route.just("/", GET), new GetEntityHandler(queryService));

        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(2);

        try {
            final ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup) //
                    .channel(NioServerSocketChannel.class)//
                    .handler(new LoggingHandler(LogLevel.DEBUG)) //
                    .childHandler(new HttpUrlAwareServerHandlerInitializer(routes)); //

            final Channel channel = serverBootstrap.bind(PORT).sync().channel();

            System.out.println("Open your web browser and navigate to " +
                    "http://127.0.0.1:" + PORT + '/');

            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
