package com.omid.grpc.server.wrapper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.jedlab.framework.spring.rest.QueryWhereParser;
import com.jedlab.framework.util.StringUtil;
import com.omid.rpc.Main;

/**
 *
 * @author Omid Pourhadi
 *
 */
public class SearchWrapper extends AbstractMessage
{

    private Main.Search search;

    public SearchWrapper(Main.Search search)
    {
        this.search = search;
    }

    @Override
    public Parser<? extends Message> getParserForType()
    {
        return search.getParserForType();
    }

    @Override
    public Message.Builder newBuilderForType()
    {
        return search.newBuilderForType();
    }

    @Override
    public Message.Builder toBuilder()
    {
        return search.toBuilder();
    }

    @Override
    public Message getDefaultInstanceForType()
    {
        return search.getDefaultInstanceForType();
    }

    @Override
    public Descriptors.Descriptor getDescriptorForType()
    {
        return search.getDescriptorForType();
    }

    @Override
    public Map<Descriptors.FieldDescriptor, Object> getAllFields()
    {
        return search.getAllFields();
    }

    @Override
    public boolean hasField(Descriptors.FieldDescriptor field)
    {
        return search.hasField(field);
    }

    @Override
    public Object getField(Descriptors.FieldDescriptor field)
    {
        return search.getField(field);
    }

    @Override
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field)
    {
        return search.getRepeatedFieldCount(field);
    }

    @Override
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index)
    {
        return search.getRepeatedField(field, index);
    }

    @Override
    public UnknownFieldSet getUnknownFields()
    {
        return search.getUnknownFields();
    }

    public <T extends AbstractMessage> T getFilter(Class<T> clz, AbstractMessage defaultInstance)
    {
        try
        {
            return search.getFilter().unpack(clz);
        }
        catch (InvalidProtocolBufferException e)
        {

        }
        return (T) defaultInstance;
    }

    public List<QueryWhereParser.FilterProperty> getJsonFilter()
    {

        try
        {
            String filter = search.getJsonFilter();
            if(StringUtil.isEmpty(filter))
                return new ArrayList<>();
            QueryWhereParser qb = filter != null ? new QueryWhereParser(URLDecoder.decode(filter, "UTF-8")) : QueryWhereParser.EMPTY;
            qb.setMatch(QueryWhereParser.AND);
            return qb.getFilterProperties();
        }
        catch (UnsupportedEncodingException e)
        {
        }
        return new ArrayList<>();
    }



}

