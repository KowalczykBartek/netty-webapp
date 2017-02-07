package com.primary.handlers.business;

import static com.google.common.util.concurrent.Futures.addCallback;
import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.datastax.driver.core.ResultSetFuture;
import com.primary.cassandra.CassandraQueryService;
import com.primary.concurrency.ConcurrencyManager;
import com.primary.domain.Entity;
import com.primary.domain.Request;
import com.primary.domain.Response;

import com.primary.utilities.CallbackConstructor;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponseStatus;

@ChannelHandler.Sharable
public class InsertEntityHandler extends SimpleChannelInboundHandler<Request> {

    private final CassandraQueryService queryService;

    public InsertEntityHandler(final CassandraQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception {

        runAsync(() -> {

            final Optional<Map> maybePayload = request.getPayload();

            final Map payload = maybePayload.get(); //FIXME assume that always exists

            //FIXME in case of exception connection will suspend ...
            final ResultSetFuture resultSetFuture = queryService.upsertEntity(extractFromMap(payload));

            addCallback(resultSetFuture, CallbackConstructor.constructCallback(ctx), ConcurrencyManager.HTTP_OPERATION_STAGE);
        }, ConcurrencyManager.HTTP_OPERATION_STAGE).whenCompleteAsync((result, throwable) -> {

            /*
             * FIXME do it better, but for now, it handles error that comes from upper Future's result.
             */
            if (throwable != null) {
                CallbackConstructor.closeWithInternalServerError(ctx);
            }

        }, ConcurrencyManager.HTTP_OPERATION_STAGE);
    }

    private Entity extractFromMap(final Map payload) {
        final String key = (String) payload.get("key");
        final String value = (String) payload.get("value");
        final UUID partition = UUID.fromString((String) payload.get("partition"));

        return Entity.of(key, value, partition);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        final Response response = new Response(HttpResponseStatus.INTERNAL_SERVER_ERROR, "Dammm....");

        ctx.writeAndFlush(response)//
                .addListener(ChannelFutureListener.CLOSE);
    }
}
