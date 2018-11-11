package com.omid.grpc.rest;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author Omid Pourhadi
 *
 */
public class DefaultRestHandler
{

    RoutingContext ctx;
    RestHandler rh;

    public DefaultRestHandler(RoutingContext ctx, RestHandler rh)
    {
        this.ctx = ctx;
        this.rh = rh;
    }

    public void handle()
    {
        ctx.response().putHeader("Content-Type", "text/plain");
        HttpMethod method = ctx.request().method();
        switch (method)
        {
        case POST:
            rh.post(ctx, ctx.getBodyAsString());
            break;
        case GET:
            rh.get(ctx);
            break;
        case PUT:
            rh.put(ctx, ctx.getBodyAsString());
            break;
        case DELETE:
            rh.delete(ctx);
            break;
        default:
            break;
        }
    }

}
