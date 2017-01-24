package com.primary.handlers;

import java.util.Map;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpUrlAwareServerHandlerInitializer extends ChannelInitializer<SocketChannel>
{
	private final Map<String, ChannelHandler> routes;

	public HttpUrlAwareServerHandlerInitializer(final Map<String, ChannelHandler> routes)
	{
		this.routes = routes;
	}

	@Override
	public void initChannel(SocketChannel ch)
	{
		ChannelPipeline p = ch.pipeline();

		p.addLast(new HttpRequestDecoder());
		p.addLast(new HttpResponseEncoder());
		p.addLast(new ResponseToHttpResponseEncoder());

		p.addLast(new BaseHttpRequestHandler(routes));
	}
}
