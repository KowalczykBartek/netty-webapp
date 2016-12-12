package com.primary.suppliers;

import java.util.function.Function;

import com.primary.domain.Request;
import com.primary.domain.Response;

import io.netty.handler.codec.http.HttpResponseStatus;

public class NotFoundSupplier implements Function<Request, Response>
{
	@Override
	public Response apply(final Request request)
	{
		return new Response(HttpResponseStatus.NOT_FOUND);
	}
}
