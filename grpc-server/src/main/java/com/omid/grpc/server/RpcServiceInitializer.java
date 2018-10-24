package com.omid.grpc.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import com.omid.grpc.annotations.GServerInterceptor;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

/**
 * @author omidp
 * 
 */
public class RpcServiceInitializer implements ApplicationContextAware, InitializingBean
{

    private ApplicationContext applicationContext;

    private ServerBuilder<?> serverBuilder;

    public RpcServiceInitializer()
    {
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (serverBuilder == null)
        {
            serverBuilder = (ServerBuilder<?>) applicationContext.getBean("nettyServer");
            Map<String, io.grpc.BindableService> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
                    io.grpc.BindableService.class);

            for (Map.Entry<String, io.grpc.BindableService> entry : beansMap.entrySet())
            {
                BindableService bs = entry.getValue();
                List<ServerInterceptor> interceptorAnnotations = processInterceptorAnnotations(bs.getClass());
                if (interceptorAnnotations.isEmpty())
                    serverBuilder.addService(bs);
                else
                    serverBuilder.addService(ServerInterceptors.intercept(bs, interceptorAnnotations));
            }

        }
        Assert.notNull(serverBuilder, "netty server can not be null");

    }

    private List<ServerInterceptor> processInterceptorAnnotations(Class<? extends BindableService> bsClz)
    {
        Set<GServerInterceptor> repeatableAnnotations = AnnotationUtils.getDeclaredRepeatableAnnotations(bsClz, GServerInterceptor.class);
        List<ServerInterceptor> interceptors = new ArrayList<>();
        if (repeatableAnnotations != null)
        {
            repeatableAnnotations.forEach(item -> {
                Class<? extends ServerInterceptor> value = item.value();
                try
                {
                    ServerInterceptor newInstance = value.newInstance();
                    interceptors.add(newInstance);
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                }
            });
        }
        return interceptors;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public ServerBuilder<?> getServerBuilder()
    {
        return serverBuilder;
    }

}
