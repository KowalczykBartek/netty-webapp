package com.primary.handlers;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.gson.Gson;
import com.primary.concurrency.ConcurrencyManager;
import com.primary.domain.Request;
import com.primary.domain.Response;

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

	private final Map<String, Function<Request, Response>> routes;

	public BaseHttpRequestHandler(final Map<String, Function<Request, Response>> routes)
	{
		this.routes = routes;
	}


	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception
	{
		if (msg instanceof HttpRequest)
		{
			HttpRequest request = (HttpRequest) msg;

			final String uri = request.uri();
			final HttpMethod method = request.method();

			final Function<Request, Response> requestResponseFunction = //
					Optional.ofNullable(routes.get(uri)).orElseGet(() -> routes.get("/404"));

			ctx.pipeline() //
					.addLast(ConcurrencyManager.HTTP_OPERATION_STAGE,//
							new UserDefinerFunctionExecutorHandler(requestResponseFunction));
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
