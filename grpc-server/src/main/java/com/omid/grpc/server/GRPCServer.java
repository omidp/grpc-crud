package com.omid.grpc.server;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.grpc.Server;

/**
 * @author omidp
 *
 */
public class GRPCServer
{
    private static final Logger logger = Logger.getLogger(GRPCServer.class.getName());

    private Server server;

    private ApplicationContext context;

    private void start() throws IOException
    {

        /* The port on which the server should run */
        int port = 50051;
        server = context.getBean(RpcServiceInitializer.class).getServer();
        server.start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                // Use stderr here since the logger may have been reset by its
                // JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                GRPCServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop()
    {
        if (server != null)
        {
            server.shutdown();
        }
        if (context != null)
        {

        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon
     * threads.
     */
    private void blockUntilShutdown() throws InterruptedException
    {
        if (server != null)
        {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException
    {
        final GRPCServer grpcServer = new GRPCServer();
        grpcServer.createSpringContext();
        grpcServer.start();
        grpcServer.blockUntilShutdown();
    }

    private void createSpringContext()
    {
        this.context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

}
