package com.omid.grpc.client;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omid.rpc.PersonFilter;

public class PersonClientStubTest
{

    GRPCClient client;

    @Before
    public void setup()
    {
        client = new GRPCClient();
    }

    @Test
//    @Ignore    
    public void testCreate()
    {
        client.createPerson("omidfdsfsd", "psdfsd", "1233242");
    }

    @Test
    @Ignore
    public void testListAndCount()
    {
        Number personCount = client.personCount(PersonFilter.newBuilder().build(), "");
        System.out.println(personCount);
        client.personList(0, 25, PersonFilter.newBuilder().build(), "");
    }

    @Test
    @Ignore
    public void testListAndCountWithFilter()
    {
        PersonFilter personFilter = PersonFilter.newBuilder().setNationalNo("1234").build();
        Number personCount = client.personCount(personFilter, "");
        System.out.println(personCount);
        client.personList(0, 25, personFilter, "");
    }

    @Test
    @Ignore
    public void update() throws InvalidProtocolBufferException
    {
        client.updatePerson(10L, "{\"id\":10L, \"nationalNo\":\"987\"}");
    }

    @Test
    @Ignore
    public void delete()
    {
        client.delete(10L);
    }

}
