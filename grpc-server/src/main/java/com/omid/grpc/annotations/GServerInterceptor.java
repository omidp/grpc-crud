package com.omid.grpc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.grpc.ServerInterceptor;

@Repeatable(GServerInterceptor.List.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface GServerInterceptor {
    Class<? extends ServerInterceptor> value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @interface List {
        GServerInterceptor[] value();
    }
}