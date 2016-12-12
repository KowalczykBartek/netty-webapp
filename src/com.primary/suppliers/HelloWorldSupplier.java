package com.primary.suppliers;

import java.util.function.Function;

import com.primary.domain.Request;
import com.primary.domain.Response;
import com.primary.repository.InMemoryDb;
import com.primary.repository.NotesRepository;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * For now should can be stateless only.
 */
@ChannelHandler.Sharable
public class HelloWorldSupplier implements Function<Request, Response>
{
	private final NotesRepository notesRepository = InMemoryDb.instance;

	@Override
	public Response apply(final Request request)
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
		return new Response(HttpResponseStatus.OK);
	}
}
