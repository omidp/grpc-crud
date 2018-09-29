package com.omid.grpc.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.google.protobuf.Int64Value;
import com.omid.rpc.Main.Pagination;
import com.omid.rpc.Main.Pagination.Builder;
import com.omid.rpc.Main.Search;
import com.omid.rpc.Person;
import com.omid.rpc.PersonFilter;
import com.omid.rpc.PersonList;
import com.omid.rpc.PersonServiceGrpc;
import com.omid.rpc.PersonServiceGrpc.PersonServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author omidp
 *
 */
public class GRPCClient implements PersonStub
{
    private static final Logger logger = Logger.getLogger(GRPCClient.class.getName());

    private ManagedChannel channel;
    private PersonServiceBlockingStub personStub;

    public GRPCClient()
    {
        this.channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50051)
                // Channels are secure by default (via SSL/TLS). For the example
                // we disable TLS to avoid
                // needing certificates.
                .usePlaintext().build();
        this.personStub = PersonServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException
    {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void createPerson(String firstName, String lastName, String nationalNo)
    {
        Person person = Person.newBuilder().setFirstName(firstName).setLastName(lastName).setNationalNo(nationalNo).build();
        personStub.create(person);
    }

    public PersonList personList(int page, int pageSize, PersonFilter filter)
    {
        Builder paginationB = Pagination.newBuilder().setPage(page).setPageSize(pageSize).setSearch(Search.newBuilder().setFilter(filter.toByteString()));        
        PersonList personList = personStub.list(paginationB.build());
        return personList;
    }

    public Number personCount(PersonFilter filter)
    {        
        return personStub.count(Search.newBuilder().setFilter(filter.toByteString()).build()).getValue();
    }

    public void updatePerson(Long id, String firstName, String lastName, String nationalNo)
    {
        Person person = Person.newBuilder().setId(id).setFirstName(firstName).setLastName(lastName).setNationalNo(nationalNo).build();
        personStub.update(person);
    }

    public void delete(Long id)
    {
        personStub.delete(Int64Value.of(id));
    }
    
    public Person findById(Long id)
    {
        Person found = personStub.findById(Int64Value.of(id));
        return found;
    }

}
