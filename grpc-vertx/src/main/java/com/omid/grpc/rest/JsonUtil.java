package com.omid.grpc.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

/**
 *
 * @author Omid Pourhadi
 *
 */
public final class JsonUtil
{

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil()
    {
    }

    public static String toJson(MessageOrBuilder msg)
    {
        try
        {
            return JsonFormat.printer().print(msg);
        }
        catch (InvalidProtocolBufferException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Message> T toMessage(String json, Message.Builder builder)
    {
        try
        {
            JsonFormat.parser().merge(json, builder);
            return (T) builder.build();
        }
        catch (InvalidProtocolBufferException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <E extends AbstractMessage> String writeEntityValueAsString(E entity)
    {
        try
        {
            return MAPPER.writeValueAsString(new EntityResponseMessage<E>("", 0, entity));
        }
        catch (JsonProcessingException e)
        {

        }
        return "unable to create json";
    }

    public static String writeResponseValueAsString()
    {
        try
        {
            return MAPPER.writeValueAsString(new ResponseMessage("", 0));
        }
        catch (JsonProcessingException e)
        {

        }
        return "unable to create json";
    }

}
