package com.omid.grpc.client;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.omid.rpc.Person;
import com.omid.rpc.Person.Builder;
import com.omid.rpc.PersonFilter;
import com.omid.rpc.PersonList;

public class PersonClientStubTest
{

    GRPCClient client;

    @Before
    public void setup()
    {
        client = new GRPCClient();
    }

    @Test
    @Ignore
    public void testCreate()
    {
        client.createPerson("omid", "p", "123");
    }

    @Test
    @Ignore
    public void testListAndCount()
    {
        PersonList personList = client.personList(0, 10, PersonFilter.getDefaultInstance());
        personList.getPersonsList().forEach(item -> {
            System.out.println("National no : " + item.getNationalNo());
        });
        Number personCount = client.personCount(PersonFilter.getDefaultInstance());
        System.out.println(personCount);
    }

    @Test
    @Ignore
    public void testListAndCountWithFilter()
    {
        PersonFilter personFilter = PersonFilter.newBuilder().setNationalNo("123").build();
        PersonList personList = client.personList(0, 10, personFilter);
        personList.getPersonsList().forEach(item -> {
            System.out.println("National no : " + item.getNationalNo());
        });
        Number personCount = client.personCount(personFilter);
        System.out.println(personCount);
    }

    @Test
    @Ignore
    public void update() throws InvalidProtocolBufferException
    {
        String json = "{\"id\":1, \"nationalNo\":\"123\"}";
        Builder personBuilder = Person.newBuilder();
        Person message = JsonUtil.<Person> toMessage(json, personBuilder);
        client.updatePerson(message.getId(), message.getFirstName(), message.getLastName(), message.getNationalNo());
    }

    @Test
    @Ignore
    public void delete()
    {
        client.delete(1L);
    }

}
