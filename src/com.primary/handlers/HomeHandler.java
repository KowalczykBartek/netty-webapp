package com.primary.handlers;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.primary.cassandra.CassandraQueryService;
import com.primary.concurrency.ConcurrencyManager;
import com.primary.domain.Request;
import com.primary.domain.Response;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;

@ChannelHandler.Sharable
public class HomeHandler extends SimpleChannelInboundHandler<Request>
{
	private final CassandraQueryService queryService;

	public HomeHandler(final CassandraQueryService queryService)
	{
		this.queryService = queryService;
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{
		supplyAsync(() -> new Response(HttpResponseStatus.NO_CONTENT, "Hello World"), ConcurrencyManager.HTTP_OPERATION_STAGE)//
				.thenAcceptAsync(response -> //
						ctx.writeAndFlush(response)//
								.addListener(ChannelFutureListener.CLOSE));
	}
}
