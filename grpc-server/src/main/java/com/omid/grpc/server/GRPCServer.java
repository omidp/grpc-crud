package com.omid.grpc.server;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

/**
 * @author omidp
 *
 */
public class GRPCServer
{
    private static final Logger logger = Logger.getLogger(GRPCServer.class.getName());

    public static final int DEFAULT_PORT = 50051;

    private Server server;

    private ApplicationContext context;

    private void start() throws IOException
    {
        ServerBuilder<?> serverBuilder = context.getBean(RpcServiceInitializer.class).getServerBuilder();
        server = serverBuilder.build();
        server.start();
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
        int port = DEFAULT_PORT;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        grpcServer.createSpringContext(port);
        grpcServer.start();
        logger.info("Server started, listening on " + port);
        grpcServer.blockUntilShutdown();
    }

    private void createSpringContext(int port)
    {
        DefaultListableBeanFactory parentBeanFactory = new DefaultListableBeanFactory();
        parentBeanFactory.registerSingleton("nettyServer", NettyServerBuilder.forPort(port));
        GenericApplicationContext parentContext = new GenericApplicationContext(parentBeanFactory);
        parentContext.refresh();
        this.context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" }, parentContext);
    }

}
