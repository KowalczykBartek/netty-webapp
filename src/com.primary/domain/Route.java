package com.primary.domain;

import io.netty.handler.codec.http.HttpMethod;
import com.google.common.base.Objects;

/**
 * Represents route for incoming request - basically this POJO works as key for <KEY><CONTROLLER_METHOD> map.
 */
public class Route {

    private final String path;
    private final HttpMethod method;

    private Route(final String path, final HttpMethod method) {
        this.path = path;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public static Route just(final String path, final HttpMethod method) {
        return new Route(path, method);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Route that = (Route) o;
        return Objects.equal(path, that.path) && //
                Objects.equal(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(path, method);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(Route.class)//
                .add("path", path)//
                .add("method", method)//
                .toString();
    }
}
