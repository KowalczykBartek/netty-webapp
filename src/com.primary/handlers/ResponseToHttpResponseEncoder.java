package com.primary.handlers;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import com.primary.domain.Response;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;


public class ResponseToHttpResponseEncoder extends MessageToMessageEncoder<Response>
{
	@Override
	protected void encode(final ChannelHandlerContext ctx, final Response msg, final List<Object> out) throws Exception
	{
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, msg.getStatus(), //
				Unpooled.copiedBuffer(msg.getBody(), CharsetUtil.UTF_8));

		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

		out.add(response);
	}
}
