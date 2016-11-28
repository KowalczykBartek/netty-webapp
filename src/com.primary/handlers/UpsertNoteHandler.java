package com.primary.handlers;

import com.primary.domain.Request;
import com.primary.repository.InMemoryDb;
import com.primary.repository.NotesRepository;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class UpsertNoteHandler extends SimpleChannelInboundHandler<Request>
{
	/*
	 * You can wonder WTF, for now I have no other idea how to perform extensible DI without DI framework,
	 * so similar approach to Cassandra is taken here.
	 */
	public static final UpsertNoteHandler instance = new UpsertNoteHandler();

	private NotesRepository notesRepository = InMemoryDb.instance;

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception
	{

		{
			//perform non blocking
			notesRepository.upsertNote("test", request);
		}

		System.err.println(request);
	}
}
