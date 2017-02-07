package com.primary.handlers;

import java.util.Map;

import com.primary.domain.Route;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpUrlAwareServerHandlerInitializer extends ChannelInitializer<SocketChannel> {
    private final Map<Route, ChannelHandler> routes;

    public HttpUrlAwareServerHandlerInitializer(final Map<Route, ChannelHandler> routes) {
        this.routes = routes;
    }

    @Override
    public void initChannel(final SocketChannel ch) {
        final ChannelPipeline p = ch.pipeline();

        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new ResponseToHttpResponseEncoder());

        p.addLast(new BaseHttpRequestHandler(routes));
    }
}
