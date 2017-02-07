package com.primary.utilities;

import com.google.common.util.concurrent.FutureCallback;
import com.primary.domain.Response;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;

public class CallbackConstructor {


    /**
     * Construct FutureCallback that return in case of success returns NO_CONTENT and in case of error
     * return INTERNAL_SERVER_ERROR
     *
     * @param ctx
     * @param <T>
     * @return FutureCallback
     */
    public static <T> FutureCallback<T> constructCallback(final ChannelHandlerContext ctx) {
        return new FutureCallback<T>() {
            @Override
            public void onSuccess(final T result) {
                final Response response = new Response(HttpResponseStatus.NO_CONTENT);

                ctx.writeAndFlush(response)//
                        .addListener(ChannelFutureListener.CLOSE);
            }

            @Override
            public void onFailure(final Throwable t) {
                closeWithInternalServerError(ctx);
            }
        };
    }

    /**
     * Close request with INTERNAL_SERVER_ERROR.
     *
     * @param ctx
     */
    public static void closeWithInternalServerError(final ChannelHandlerContext ctx) {
        final Response response = new Response(HttpResponseStatus.INTERNAL_SERVER_ERROR);

        ctx.writeAndFlush(response)//
                .addListener(ChannelFutureListener.CLOSE);
    }
}
