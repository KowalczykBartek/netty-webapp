package com.primary.handlers.userrelatedhandlers;

import com.google.gson.Gson;
import com.primary.domain.Request;
import com.primary.domain.Response;
import com.primary.repository.InMemoryDb;
import com.primary.repository.NotesRepository;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;

@ChannelHandler.Sharable
public class GetNoteHandler extends SimpleChannelInboundHandler<Request>
{
	//Gson instances are Thread-safe
	private static Gson gson = new Gson();

	public static final GetNoteHandler instance = new GetNoteHandler();

	private final NotesRepository notesRepository = InMemoryDb.instance;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{
		/*
		 * we have passing between thread-pools. But thanks to barrier fence released by "final" inside this object,
		 * everything is ok.
		 */
		ctx.writeAndFlush(new Response(HttpResponseStatus.OK, gson.toJson(notesRepository.getNote("Janush"))))//
				.addListener(ChannelFutureListener.CLOSE);
	}
}
