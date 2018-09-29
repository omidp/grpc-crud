package com.omid.grpc.service;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;


public class AuditAwareImpl implements AuditorAware<String>
{

    public Optional<String> getCurrentAuditor()
    {
        return Optional.empty();
    }

}
