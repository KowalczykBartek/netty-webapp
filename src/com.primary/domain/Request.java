package com.primary.domain;

import java.util.Map;
import java.util.Optional;

public class Request
{
	private final String user;
	private final Optional<Map> payload;

	public Request(final String user, final Optional<Map> payload)
	{
		this.user = user;
		this.payload = payload;
	}

	public String getUser()
	{
		return user;
	}

	public Optional<Map> getPayload()
	{
		return payload;
	}

	//equals missing
	//hash code missing
	//toString missing
}
