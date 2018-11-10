package org.primefaces;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.google.protobuf.Int64Value;
import com.omid.rpc.Main.Search;
import com.omid.rpc.PersonServiceGrpc;
import com.omid.rpc.PersonServiceGrpc.PersonServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@ManagedBean(name="helloGrpc")
@RequestScoped
public class HelloGrpc implements Serializable
{

    
    private ManagedChannel channel;
    private PersonServiceBlockingStub personStub;

    public HelloGrpc()
    {
        this.channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50051)
                // Channels are secure by default (via SSL/TLS). For the example
                // we disable TLS to avoid
                // needing certificates.
                .usePlaintext().build();
        this.personStub = PersonServiceGrpc.newBlockingStub(channel);
    }
    
    public void callGrpc()
    {

        System.out.println("Calling Grpc");
        Int64Value count = personStub.count(Search.newBuilder().build());
        System.out.println(count.getValue());
    }

}
