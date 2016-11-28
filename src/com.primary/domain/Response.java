package com.primary.domain;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Response
{
	private final HttpResponseStatus status;
	private final String body;

	public Response(final HttpResponseStatus status, final String body)
	{
		this.status = status;
		this.body = body;
	}

	public HttpResponseStatus getStatus()
	{
		return status;
	}

	public String getBody()
	{
		return body;
	}

	//equals missing
	//hash code missing
	//toString missing
}
