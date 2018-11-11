package com.omid.grpc.handlers;

import com.google.protobuf.Any;
import com.google.protobuf.Int64Value;
import com.omid.grpc.rest.DefaultRestHandler;
import com.omid.grpc.rest.JsonUtil;
import com.omid.grpc.rest.PaginationBuilderWrapper;
import com.omid.grpc.rest.RestHandler;
import com.omid.rpc.Main;
import com.omid.rpc.Person;
import com.omid.rpc.PersonFilter;
import com.omid.rpc.PersonList;
import com.omid.rpc.PersonServiceGrpc;
import com.omid.rpc.PersonServiceGrpc.PersonServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Omid Pourhadi
 */
public interface PersonHandler extends Handler<RoutingContext>
{

    static PersonHandler create(ManagedChannel managedChannel)
    {
        return new PersonHandlerImpl(managedChannel);
    }

    class PersonHandlerImpl implements PersonHandler, RestHandler
    {

        private PersonServiceBlockingStub personStub;

        public PersonHandlerImpl(ManagedChannel managedChannel)
        {
            personStub = PersonServiceGrpc.newBlockingStub(managedChannel);
        }

        @Override
        public void handle(RoutingContext event)
        {
            new DefaultRestHandler(event, this).handle();
        }

        @Override
        public void post(RoutingContext ctx, String bodyAsString)
        {
            Person.Builder personBuilder = Person.newBuilder();
            Person message = JsonUtil.<Person>toMessage(bodyAsString, personBuilder);
            Person person = personStub.create(message);
            ctx.response().end(JsonUtil.writeEntityValueAsString(person));
        }

        @Override
        public void get(RoutingContext ctx)
        {

            PaginationBuilderWrapper pw = new PaginationBuilderWrapper(ctx);
            Main.Pagination.Builder pb = Main.Pagination.newBuilder();
            pb.setPage(pw.page()).setPageSize(pw.pageSize()).setSearch(pw.search(Any.pack(PersonFilter.newBuilder().build())))
                    .addAllSortParam(ctx.queryParam("sort"));
            String id = ctx.pathParam("param0");
            if (id == null || id.trim().length() == 0)
            {
                PersonList list = personStub.list(pb.build());
                ctx.response().end(JsonUtil.toJson(list));
            }
            else
            {
                Person person = personStub.findById(Int64Value.newBuilder().setValue(Long.parseLong(id)).build());
                ctx.response().end(JsonUtil.writeEntityValueAsString(person));
            }

        }

        @Override
        public void put(RoutingContext ctx, String bodyAsString)
        {
            String id = ctx.pathParam("param0");
            if (id != null && id.trim().length() > 0)
            {
                Main.UpdateValue updateValue = Main.UpdateValue.newBuilder().setId(Long.parseLong(id)).setJsonValue(bodyAsString).build();
                Person update = personStub.update(updateValue);
                ctx.response().end(JsonUtil.writeEntityValueAsString(update));
            }
            else
                ctx.response().end("");
        }

        @Override
        public void delete(RoutingContext ctx)
        {
            String id = ctx.pathParam("param0");
            if (id != null && id.trim().length() > 0)
                personStub.delete(Int64Value.newBuilder().setValue(Long.parseLong(id)).build());
            ctx.response().end(JsonUtil.writeResponseValueAsString());
        }
    }
}
