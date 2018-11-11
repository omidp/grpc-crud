package com.omid.grpc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.github.lalyos.jfiglet.FigletFont;
import com.omid.grpc.handlers.IndexHandler;
import com.omid.grpc.handlers.PersonHandler;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class GrpcxVerticle extends AbstractVerticle
{

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcxVerticle.class);
    
    
    @Override
    public void start(Future<Void> startFuture) throws Exception
    {
        String asciiArt = FigletFont.convertOneLine("GRPC VERT.X CLIENT");
        LOGGER.info(asciiArt);
        startHttpServer().setHandler(ar -> {
            if (ar.succeeded())
                startFuture.complete();
            else
                startFuture.fail(ar.cause());
        });
        LOGGER.info("server started at : 8081");
    }

    public static void main(String[] args)
    {
        Runner.runExample(GrpcxVerticle.class);
    }




    private Future<Void> startHttpServer()
    {
        ManagedChannel managedChannel = NettyChannelBuilder.forAddress("127.0.0.1", 50051).usePlaintext()
                .build();
        Future<Void> future = Future.future();
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        //
        router.post().handler(BodyHandler.create());
        router.put().handler(BodyHandler.create());
        //
        Set<HttpMethod> methods = new HashSet<>(Arrays
                .asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.OPTIONS, HttpMethod.DELETE, HttpMethod.PATCH));

        router.route().handler(CorsHandler.create("*").allowedMethods(methods));
        router.route("/static/*").handler(StaticHandler.create());
        
        
        
            router.route("/*").handler(LoggerHandler.create());
        
        //

        router.route("/").handler(IndexHandler.create());

        router.routeWithRegex("/api/v1/secure/persons|/api/v1/secure/persons/(.*?)").pathRegex("/api/v1/secure/persons|/api/v1/secure/persons/(.*?)").handler(PersonHandler.create(managedChannel));
        //
        httpServer.requestHandler(router::accept).listen(8081, ar -> {
            if (ar.succeeded())
                future.complete();
            else
                future.fail(ar.cause());
        });
        return future;
    }
    
}
