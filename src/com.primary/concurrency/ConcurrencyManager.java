package com.primary.concurrency;

import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class ConcurrencyManager
{
	/*
	 * Cassandra's stage approach - replace later with real manager.
	 */
	public static final EventExecutorGroup GET_STAGE = new DefaultEventExecutorGroup(15);
	public static final EventExecutorGroup PUT_STAGE = new DefaultEventExecutorGroup(15);
}
