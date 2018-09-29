package com.omid.grpc.client;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.omid.rpc.Person;
import com.omid.rpc.Person.Builder;

public class JsonTest
{

    @Test
    public void toJson() throws InvalidProtocolBufferException
    {
        System.out.println(JsonUtil.toJson(Person.newBuilder().setId(100L).setNationalNo("123").build()));

    }

    @Test
    public void fromJson() throws InvalidProtocolBufferException
    {
        String json = "{\"id\":1, \"nationalNo\":\"123\"}";
        Builder personBuilder = Person.newBuilder();
        Person message = JsonUtil.<Person> toMessage(json, personBuilder);
        System.out.println(message.getId());
    }

}
