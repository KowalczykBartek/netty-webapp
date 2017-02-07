package com.primary.handlers.business;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.google.common.util.concurrent.FutureCallback;
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

import static com.google.common.util.concurrent.Futures.addCallback;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.primary.utilities.CallbackConstructor.closeWithInternalServerError;
import static java.util.concurrent.CompletableFuture.runAsync;

@ChannelHandler.Sharable
public class GetEntityHandler extends SimpleChannelInboundHandler<Request> {

    private final CassandraQueryService queryService;

    public GetEntityHandler(final CassandraQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Request request) throws Exception {
        runAsync(() -> {

            /**
             * FIXME for get not Optional.empty must be returned...
             */

            //fixme remove duplication
            final Optional<Map> maybePayload = request.getPayload();

            final Map payload = maybePayload.get(); //FIXME assume that always exists

            final Entity entity = extractFromMap(payload);

            final ResultSetFuture valueForKeyFromPartition = //
                    queryService.getValueForKeyFromPartition(entity.getPartition(), entity.getKey());


            final FutureCallback<ResultSet> futureCallback = new FutureCallback<ResultSet>() {
                @Override
                public void onSuccess(final ResultSet result) {
                    final Response response = new Response(HttpResponseStatus.NO_CONTENT);

                    ctx.writeAndFlush(response)//
                            .addListener(ChannelFutureListener.CLOSE);
                }

                @Override
                public void onFailure(final Throwable t) {
                    closeWithInternalServerError(ctx);
                }
            };

            addCallback(valueForKeyFromPartition, futureCallback, ConcurrencyManager.HTTP_OPERATION_STAGE);

        }, ConcurrencyManager.HTTP_OPERATION_STAGE).whenCompleteAsync((result, throwable) -> {

            /*
             * FIXME do it better, but for now, it handles error that comes from upper Future's result.
             */
            if (throwable != null) {
                CallbackConstructor.closeWithInternalServerError(ctx);
            }

        }, ConcurrencyManager.HTTP_OPERATION_STAGE);

    }

    //fixme remove duplication !
    private Entity extractFromMap(final Map payload) {
        final String key = (String) payload.get("key");
        final String value = (String) payload.get("value");
        final UUID partition = UUID.fromString((String) payload.get("partition"));

        return Entity.of(key, value, partition);
    }

}
