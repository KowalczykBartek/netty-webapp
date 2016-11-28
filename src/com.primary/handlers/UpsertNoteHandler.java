package com.primary.handlers;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.primary.domain.Request;
import com.primary.repository.InMemoryDb;
import com.primary.repository.NotesRepository;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;

@ChannelHandler.Sharable
public class UpsertNoteHandler extends SimpleChannelInboundHandler<Request>
{
	/*
	 * You can wonder WTF, for now I have no other idea how to perform extensible DI without DI framework,
	 * so similar approach to Cassandra is taken here.
	 */
	public static final UpsertNoteHandler instance = new UpsertNoteHandler();

	private final NotesRepository notesRepository = InMemoryDb.instance;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{
		{
			//perform non blocking
			notesRepository.upsertNote(request.getUser(), request.getPayload());
		}

		System.err.println(request);

		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);

		ctx.writeAndFlush(response)//
				.addListener(ChannelFutureListener.CLOSE);
	}
}
