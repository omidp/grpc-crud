package com.omid.grpc.server;


import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

public class AuthenticationServerInterceptor implements ServerInterceptor
{
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next)
    {
        String methodName = call.getMethodDescriptor().getFullMethodName();
        if (needAuthentication(methodName) && !authnPassed(requestHeaders))
        {
            call.close(Status.UNAUTHENTICATED.withDescription("not authenticated"), new Metadata());
        }
        return next.startCall(call, requestHeaders);
    }

    private boolean needAuthentication(String methodName)
    {
        return false;
    }

    private boolean authnPassed(Metadata requestHeaders)
    {
        return false;
    }
}