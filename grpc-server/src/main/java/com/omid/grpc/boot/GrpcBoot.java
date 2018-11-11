package com.omid.grpc.boot;

import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.jedlab.framework.spring.SpringUtil;
import com.omid.grpc.server.RpcServiceInitializer;

@SpringBootApplication
@PropertySource({ "classpath:database.properties" })
public class GrpcBoot implements CommandLineRunner
{

    private static final Logger logger = Logger.getLogger(GrpcBoot.class.getName());

    public static final int DEFAULT_PORT = 50051;

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(GrpcBoot.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        int port = DEFAULT_PORT;
        if (args.length > 0)
        {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].startsWith("--port="))
                    port = Integer.parseInt(args[i].substring("--port=".length()));
            }
        }
        new GrpcNettyWebServer(rpcInit(), port).start();
        logger.info("server started at port : " + port);
    }

    @Autowired
    EntityManagerFactory entityManagerFactory;
    

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager()
    {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setNestedTransactionAllowed(true);
        tm.setEntityManagerFactory(entityManagerFactory);
        return tm;
    }

    @Bean
    SpringUtil springUtil()
    {
        return new SpringUtil();
    }

    @Bean
    RpcServiceInitializer rpcInit()
    {
        return new RpcServiceInitializer();
    }

}
