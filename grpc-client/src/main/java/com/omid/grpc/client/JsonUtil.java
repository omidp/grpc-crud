package com.omid.grpc.client;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

/**
 * @author omidp
 *
 */
public final class JsonUtil
{

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
    
}
