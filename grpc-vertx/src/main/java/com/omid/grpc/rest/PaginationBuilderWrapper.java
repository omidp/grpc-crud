package com.omid.grpc.rest;

import java.util.List;

import com.google.protobuf.Any;
import com.omid.rpc.Main;

import io.vertx.ext.web.RoutingContext;

/**
 * @author Omid Pourhadi
 */
public class PaginationBuilderWrapper
{

    private RoutingContext ctx;

    public PaginationBuilderWrapper(RoutingContext ctx)
    {
        this.ctx = ctx;
    }

    public int page()
    {
        List<String> page = ctx.queryParam("page");
        if (page != null && page.size() > 0)
            return Integer.parseInt(page.iterator().next());
        else
            return 0;
    }

    public int pageSize()
    {
        List<String> page = ctx.queryParam("pageSize");
        if (page != null && page.size() > 0)
            return Integer.parseInt(page.iterator().next());
        else
            return 10;
    }

    public String filter()
    {
        List<String> page = ctx.queryParam("filter");
        if (page != null && page.size() > 0)
            return page.iterator().next();
        else
            return "";
    }

    public Main.Search search(Any filter)
    {
        Main.Search.Builder builder = Main.Search.newBuilder();
        builder.setJsonFilter(filter());
        if (filter != null)
            builder.setFilter(filter);
        return builder.build();

    }

}
