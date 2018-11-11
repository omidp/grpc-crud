package com.omid.grpc.boot;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author Omid Pourhadi
 *
 */
@Configuration
@EntityScan({ "com.omid.grpc.boot.domain" })
@EnableJpaRepositories({ "com.omid.grpc.boot.dao" })
public class JpaDataConfig
{



}
