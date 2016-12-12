package com.primary.handlers;

import java.util.Map;
import java.util.function.Function;

import com.primary.domain.Request;
import com.primary.domain.Response;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpUrlAwareServerHandlerInitializer extends ChannelInitializer<SocketChannel>
{
	private final Map<String, Function<Request, Response>> routes;

	public HttpUrlAwareServerHandlerInitializer(final Map<String, Function<Request, Response>> routes)
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
