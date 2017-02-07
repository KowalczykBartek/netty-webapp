package com.primary.domain;

import com.google.common.base.Objects;

import java.util.Map;
import java.util.Optional;

public class Request {
    private final Optional<Map> payload;

    public Request(final Optional<Map> payload) {
        this.payload = payload;
    }

    public Optional<Map> getPayload() {
        return payload;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Request that = (Request) o;
        return Objects.equal(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(payload);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(Route.class)//
                .add("payload", payload)//
                .toString();
    }
}
