package com.primary.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraQueryService
{
	/**
	 * We want to give user ability to store  key-value data in its own "partition"
	 *
	 * Fixme checks for partition size needed
	 */
	private static final String CREATE_SAMPLE_TABLE = //
			"CREATE TABLE IF NOT EXISTS Storage.packages(" + //
					"package timeuuid, " + //
					"key text, " + //
					"value text, " + //
					"PRIMARY KEY (package))";

	private final Object lock = new Object();

	private volatile Cluster cluster;
	private volatile Session session;

	private final String contactPoint;

	public CassandraQueryService(final String contactPoint)
	{
		//fixme cluster with multiple contact points should be supported.
		this.contactPoint = contactPoint;
	}

	/**
	 * Method responsible for cluster connection and session initialization. Additionally method will ensure that
	 * all required tables are created in desired keyspace. Call is blocking.
	 *
	 * State being set is guarded by internal lock not exposed for external usage.
	 */
	public void initialize()
	{
		synchronized (lock)
		{
			if (cluster == null && session == null)
			{
				cluster = Cluster.builder().addContactPoint(contactPoint).build();
				session = cluster.connect();

				//initialize tables
				session.execute(CREATE_SAMPLE_TABLE);
			}
		}
	}
}
