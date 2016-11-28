package com.primary.handlers.userrelatedhandlers;

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
			notesRepository.upsertNote(request.getUser(), request.getPayload().get());
		}

		System.err.println(request);

		/*
		 * we have passing between thread-pools. But thanks to barrier fence released by "final" inside this object,
		 * everything is ok.
		 */
		ctx.writeAndFlush(new Response(HttpResponseStatus.OK, ""))//
				.addListener(ChannelFutureListener.CLOSE);
	}
}
