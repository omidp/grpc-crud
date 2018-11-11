package com.omid.grpc.rest;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.google.protobuf.AbstractMessage;

/**
 *
 * @author Omid Pourhadi
 *
 */
public class EntityResponseMessage<E extends AbstractMessage> extends ResponseMessage
{

    private E result;

    public EntityResponseMessage()
    {
    }

    public EntityResponseMessage(String message, int code, E entity)
    {
        super(message, code);
        this.result = entity;
    }

    @JsonRawValue
    public String getResult()
    {
        return JsonUtil.toJson(result);
    }

}