package com.omid.grpc.handlers;

import com.omid.grpc.rest.DefaultRestHandler;
import com.omid.grpc.rest.RestHandler;

import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author Omid Pourhadi
 *
 */
public interface IndexHandler extends Handler<RoutingContext>
{
    static IndexHandler create()
    {
        return new IndexHandlerImpl();
    }

    class IndexHandlerImpl implements IndexHandler, RestHandler
    {

        private static final Logger LOGGER = LoggerFactory.getLogger(IndexHandler.class);

        @Override
        public void handle(RoutingContext event)
        {
            new DefaultRestHandler(event, this).handle();
        }

        @Override
        public void post(RoutingContext ctx, String bodyAsString)
        {
            LOGGER.info(bodyAsString);
            ctx.response().end("post");
        }

        @Override
        public void get(RoutingContext ctx)
        {
            ctx.response().end("get");
        }

        @Override
        public void put(RoutingContext ctx, String bodyAsString)
        {

            LOGGER.info(bodyAsString);
            ctx.response().end("put");
        }

        @Override
        public void delete(RoutingContext ctx)
        {
            ctx.response().end("delete");
        }
    }
}

