package com.primary.domain;

import java.util.UUID;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

public class Entity {
    private final String key;
    private final String value;
    private final UUID partition;

    private Entity(final String key, final String value, final UUID partition) {
        this.key = key;
        this.value = value;
        this.partition = partition;
    }

    public static Entity of(final String key, final String value, final UUID partition) {
        Preconditions.checkArgument(!StringUtils.isEmpty(key));
        Preconditions.checkArgument(!StringUtils.isEmpty(value));
        Preconditions.checkNotNull(partition);

        return new Entity(key, value, partition);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public UUID getPartition() {
        return partition;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Entity that = (Entity) o;
        return Objects.equal(key, that.key) && //
                Objects.equal(value, that.value) &&//
                Objects.equal(partition, that.partition);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, value, partition);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(Route.class)//
                .add("key", key)//
                .add("value", value)//
                .add("partition", partition)//
                .toString();
    }
}
