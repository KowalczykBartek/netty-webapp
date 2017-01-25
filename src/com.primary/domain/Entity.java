package com.primary.domain;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

public class Entity
{
	private final String key;
	private final String value;
	private final UUID partition;

	private Entity(final String key, final String value, final UUID partition)
	{
		this.key = key;
		this.value = value;
		this.partition = partition;
	}

	public static Entity of(final String key, final String value, final UUID partition)
	{
		Preconditions.checkArgument(!StringUtils.isEmpty(key));
		Preconditions.checkArgument(!StringUtils.isEmpty(value));
		Preconditions.checkNotNull(partition);

		return new Entity(key, value, partition);
	}

	public String getKey()
	{
		return key;
	}

	public String getValue()
	{
		return value;
	}

	public UUID getPartition()
	{
		return partition;
	}

	//equals missing
	//hash code missing
	//toString missing
}
