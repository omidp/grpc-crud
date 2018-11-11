package com.omid.grpc.boot;

import java.io.IOException;

import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;

import com.jedlab.framework.util.CollectionUtil;
import com.omid.grpc.server.RpcServiceInitializer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

public class GrpcNettyWebServer implements WebServer
{

    

    private Server server;
    private final RpcServiceInitializer rpcInit;
    private final int DEFAULT_PORT; 

    public GrpcNettyWebServer(RpcServiceInitializer rpcInit, int port)
    {
        this.rpcInit = rpcInit;
        this.DEFAULT_PORT = port;
    }

    @Override
    public void start() throws WebServerException
    {
        ServerBuilder<?> serverBuilder = NettyServerBuilder.forPort(DEFAULT_PORT);
        rpcInit.getBindaleServiceList().stream()
        .filter(i->CollectionUtil.isEmpty(i.getInterceptors()))
        .forEach(item->serverBuilder.addService(item.getBindableService()));
        rpcInit.getBindaleServiceList().stream()
        .filter(i->CollectionUtil.isNotEmpty(i.getInterceptors()))
        .forEach(item->serverBuilder.addService(ServerInterceptors.intercept(item.getBindableService(), item.getInterceptors())));
        server = serverBuilder.build();

        try
        {
            server.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                // Use stderr here since the logger may have been reset by its
                // JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GrpcNettyWebServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
        startDaemonAwaitThread();

    }

    @Override
    public void stop() throws WebServerException
    {
        if (server != null)
        {
            server.shutdown();
        }
    }

    @Override
    public int getPort()
    {
        return DEFAULT_PORT;
    }

    private void startDaemonAwaitThread()
    {
        Thread awaitThread = new Thread("container-") {

            @Override
            public void run()
            {
                try
                {
                    GrpcNettyWebServer.this.server.awaitTermination();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

        };
        awaitThread.setContextClassLoader(getClass().getClassLoader());
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    
}
