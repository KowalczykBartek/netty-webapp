package com.primary.handlers;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.google.gson.Gson;
import com.primary.domain.Request;
import com.primary.repository.InMemoryDb;
import com.primary.repository.NotesRepository;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;

public class GetNoteHandler extends SimpleChannelInboundHandler<Request>
{
	//Gson instances are Thread-safe
	private static Gson gson = new Gson();

	public static final GetNoteHandler instance = new GetNoteHandler();

	private NotesRepository notesRepository = InMemoryDb.instance;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{

		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, //
				Unpooled.copiedBuffer(gson.toJson(notesRepository.getNote("Janush")), CharsetUtil.UTF_8));

		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

		ctx.writeAndFlush(response)//
				.addListener(ChannelFutureListener.CLOSE);
	}
}
