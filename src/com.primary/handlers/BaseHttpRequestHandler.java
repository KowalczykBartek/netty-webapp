package com.primary.handlers;

import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.primary.concurrency.ConcurrencyManager;
import com.primary.domain.Request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class BaseHttpRequestHandler extends SimpleChannelInboundHandler<Object>
{
	//Gson instances are Thread-safe
	private static Gson gson = new Gson();

	private final StringBuilder buf = new StringBuilder();

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception
	{
		if (msg instanceof HttpRequest)
		{
			HttpRequest request = (HttpRequest) msg;

			final String uri = request.uri();
			final HttpMethod method = request.method();

			//fixme - refactor
			if (method.equals(HttpMethod.GET))
			{
				ctx.pipeline() //
						.addLast(ConcurrencyManager.GET_STAGE, GetNoteHandler.instance);
			}
			else if (method.equals(HttpMethod.PUT))
			{
				ctx.pipeline() //
						.addLast(ConcurrencyManager.PUT_STAGE, UpsertNoteHandler.instance);
			}
			else
			{
				//implement 404 or something :D
			}
		}

		/*
		 * We have proper Handler assembled to pipeline, now we have only pass object.
		 */

		if (msg instanceof HttpContent)
		{
			HttpContent httpContent = (HttpContent) msg;

			buf.append(msg);

			if (msg instanceof LastHttpContent)
			{
				if (msg.equals(LastHttpContent.EMPTY_LAST_CONTENT))
				{
					ctx.fireChannelRead(new Request("Janush", Optional.<Map>empty()));
				}
				else
				{
					//should event such json processing be made in another thread pool ?
					final Map map = gson.fromJson(httpContent.content().toString(CharsetUtil.UTF_8), Map.class);

					ctx.fireChannelRead(new Request("Janush", Optional.of(map)));
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
		ctx.close();
	}
}
