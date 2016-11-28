package com.primary.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpUrlAwareServerHandlerInitializer extends ChannelInitializer<SocketChannel>
{
	@Override
	public void initChannel(SocketChannel ch)
	{
		ChannelPipeline p = ch.pipeline();

		p.addLast(new HttpRequestDecoder());
		p.addLast(new HttpResponseEncoder());

		p.addLast(new BaseHttpRequestHandler());
	}
}
