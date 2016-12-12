package com.primary.handlers;

import java.util.function.Function;

import com.primary.domain.Request;
import com.primary.domain.Response;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class UserDefinerFunctionExecutorHandler extends SimpleChannelInboundHandler<Request>
{
	final Function<Request, Response> requestHandler;

	public UserDefinerFunctionExecutorHandler(final Function<Request, Response> requestHandler)
	{
		this.requestHandler = requestHandler;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{
		//FIXME how to protect against long-running/blocking calls ?
		ctx.writeAndFlush(requestHandler.apply(request))//
				.addListener(ChannelFutureListener.CLOSE);
	}
}
