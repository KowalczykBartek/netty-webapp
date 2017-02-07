package com.primary.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.primary.domain.Entity;

import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

/**
 * Service responsible for interactions with Cassandra. This class for each instance holds its own Cluster and Session
 * state for connections. So this class should be used/create as singleton - and because of is Thread Safe can be accessed
 * from multiple threads.
 */
public class CassandraQueryService {
    private static final String PACKAGE_TABLE_NAME = "packages";
    private static final String STORAGE_KEYSPACE_NAME = "Storage";

    private static final String PARTITION_COLUMN_NAME = "partition";
    private static final String KEY_COLUMN_NAME = "key";
    private static final String VALUE_COLUMN_NAME = "value";

    /**
     * We want to give user ability to store  key-value data in its own "partition"
     */
    private static final String CREATE_SAMPLE_TABLE = //
            "CREATE TABLE IF NOT EXISTS " + STORAGE_KEYSPACE_NAME + "." + PACKAGE_TABLE_NAME + "(" + //
                    PARTITION_COLUMN_NAME + " uuid, " + //
                    KEY_COLUMN_NAME + " text, " + //
                    VALUE_COLUMN_NAME + " text, " + //
                    "PRIMARY KEY ((" + PARTITION_COLUMN_NAME + ")," + KEY_COLUMN_NAME + "))";

    private final Object lock = new Object(); //guards internal Session and Cluster state.

    private volatile Cluster cluster;
    private volatile Session session;

    private final String contactPoint;

    private CassandraQueryService(final String contactPoint) {
        this.contactPoint = contactPoint;
    }

    /**
     * Build and initialize CassandraQueryService. Operation is blocking - it waits for cluster initialization.
     *
     * @param contactPoint - address of connection point.
     * @return initialized CassandraQueryService instance.
     */
    public static CassandraQueryService build(final String contactPoint) {
        final CassandraQueryService cassandraQueryService = new CassandraQueryService(contactPoint);
        cassandraQueryService.initialize();
        return cassandraQueryService;
    }


    /**
     * Update or create key-value pair with given partition. Operation is performed in non blocking way.
     *
     * @param entity
     * @return ResultSetFuture of possible, not finished query.
     */
    public ResultSetFuture upsertEntity(final Entity entity) {
        final Statement statement = insertInto(STORAGE_KEYSPACE_NAME, PACKAGE_TABLE_NAME)//
                .value(KEY_COLUMN_NAME, entity.getKey()) //
                .value(VALUE_COLUMN_NAME, entity.getValue()) //
                .value(PARTITION_COLUMN_NAME, entity.getPartition()); //

        return session.executeAsync(statement);
    }

    /**
     * Perform get query on underlying database.  Operation is performed in non blocking way.
     *
     * @param uuid of partition.
     * @return ResultSetFuture of possible, not finished query.
     */
    public ResultSetFuture getValueForKeyFromPartition(final UUID uuid, final String key) {
        final Statement statement = select(KEY_COLUMN_NAME, VALUE_COLUMN_NAME, PACKAGE_TABLE_NAME)//
                .from(STORAGE_KEYSPACE_NAME, PACKAGE_TABLE_NAME)//
                .where(eq(PARTITION_COLUMN_NAME, uuid))//
                .and(eq(KEY_COLUMN_NAME, key));


        return session.executeAsync(statement);
    }

    /**
     * Method responsible for cluster connection and session initialization. Additionally method will ensure that
     * all required tables are created in desired keyspace. Call is blocking.
     * <p>
     * State being set is guarded by internal lock not exposed for external usage.
     */
    private void initialize() {
        synchronized (lock) {
            if (cluster == null && session == null) {
                cluster = Cluster.builder().addContactPoint(contactPoint).build();
                session = cluster.connect();

                //initialize tables
                session.execute(CREATE_SAMPLE_TABLE);
            }
        }
    }
}
