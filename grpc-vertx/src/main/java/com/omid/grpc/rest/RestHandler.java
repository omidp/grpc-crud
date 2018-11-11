package com.omid.grpc.rest;

import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author Omid Pourhadi
 *
 */
public interface RestHandler
{

    public void post(RoutingContext ctx, String bodyAsString);

    public void get(RoutingContext ctx);

    public void put(RoutingContext ctx, String bodyAsString);

    public void delete(RoutingContext ctx);

}
