package com.primary.handlers;

import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.primary.domain.Request;

import com.primary.domain.Route;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class BaseHttpRequestHandler extends SimpleChannelInboundHandler<Object> {
    //Gson instances are Thread-safe
    private static Gson gson = new Gson();

    private final StringBuilder buf = new StringBuilder();

    private final Map<Route, ChannelHandler> routes;

    public BaseHttpRequestHandler(final Map<Route, ChannelHandler> routes) {
        this.routes = routes;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            final String uri = request.uri();
            final HttpMethod method = request.method();

            final ChannelHandler handler = //
                    Optional.ofNullable(routes.get(Route.just("/", method)))
                            .orElseThrow(() -> new RuntimeException("No routes defined"));

            ctx.pipeline() //
                    .addLast(handler);
        }

		/*
         * We have proper Handler assembled to pipeline, now we have only pass object.
		 */

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            buf.append(msg);

            if (msg instanceof LastHttpContent) {
                if (msg.equals(LastHttpContent.EMPTY_LAST_CONTENT)) {
                    ctx.fireChannelRead(new Request(Optional.empty()));
                } else {
                    //should event such json processing be made in another thread pool ?
                    final Map map = gson.fromJson(httpContent.content().toString(CharsetUtil.UTF_8), Map.class);

                    ctx.fireChannelRead(new Request(Optional.of(map)));
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
