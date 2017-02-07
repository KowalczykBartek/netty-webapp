package com.primary.domain;

import com.google.common.base.Objects;
import io.netty.handler.codec.http.HttpResponseStatus;

public class Response {
    private final HttpResponseStatus status;
    private final String body;

    public Response(final HttpResponseStatus status, final String body) {
        this.status = status;
        this.body = body;
    }

    public Response(final HttpResponseStatus status) {
        this(status, "");
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Response that = (Response) o;
        return Objects.equal(status, that.status) && //
                Objects.equal(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status, body);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(Route.class)//
                .add("status", status)//
                .add("body", body)//
                .toString();
    }
}
