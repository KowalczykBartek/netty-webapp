package com.primary.handlers;

import com.primary.domain.Request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GetNoteHandler extends SimpleChannelInboundHandler<Request>
{
	public static final GetNoteHandler instance = new GetNoteHandler();

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{

	}
}
