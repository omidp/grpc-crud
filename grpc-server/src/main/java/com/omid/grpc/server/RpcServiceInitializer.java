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

    private List<BindaleServiceHolder> bindaleServiceList;

    public RpcServiceInitializer()
    {
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (bindaleServiceList == null)
            bindaleServiceList = new ArrayList<>();
           
            Map<String, io.grpc.BindableService> beansMap = BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext,
                    io.grpc.BindableService.class);

            for (Map.Entry<String, io.grpc.BindableService> entry : beansMap.entrySet())
            {
                BindableService bs = entry.getValue();
                List<ServerInterceptor> interceptorAnnotations = processInterceptorAnnotations(bs.getClass());
                if (interceptorAnnotations.isEmpty())
                    bindaleServiceList.add(new BindaleServiceHolder(bs));
                else
                    bindaleServiceList.add(new BindaleServiceHolder(interceptorAnnotations, bs));
                    
            }


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
    
    

    public List<BindaleServiceHolder> getBindaleServiceList()
    {
        return bindaleServiceList;
    }



    public static class BindaleServiceHolder
    {
        List<ServerInterceptor> interceptors;
        BindableService bindableService;

        public BindaleServiceHolder(List<ServerInterceptor> interceptors, BindableService bindableService)
        {
            this.interceptors = interceptors;
            this.bindableService = bindableService;
        }
        
        

        public BindaleServiceHolder(BindableService bindableService)
        {
            this.bindableService = bindableService;
        }



        public List<ServerInterceptor> getInterceptors()
        {
            return interceptors;
        }

        public BindableService getBindableService()
        {
            return bindableService;
        }

    }

}
