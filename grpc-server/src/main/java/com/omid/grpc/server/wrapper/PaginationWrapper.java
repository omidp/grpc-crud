package com.omid.grpc.server.wrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.data.domain.Sort;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.jedlab.framework.spring.rest.QueryWhereParser;
import com.omid.rpc.Main;

/**
 *
 * @author Omid Pourhadi
 *
 */
public class PaginationWrapper extends AbstractMessage
{

    public static final int PAGE_SIZE = 15;

    private Main.Pagination pagination;
    private SearchWrapper searchW;

    public PaginationWrapper(Main.Pagination pagination)
    {
        this.pagination = pagination;
        this.searchW = new SearchWrapper(pagination.getSearch());
    }

    @Override
    public Parser<? extends Message> getParserForType()
    {
        return pagination.getParserForType();
    }

    @Override
    public Message.Builder newBuilderForType()
    {
        return pagination.newBuilderForType();
    }

    @Override
    public Message.Builder toBuilder()
    {
        return pagination.toBuilder();
    }

    @Override
    public Message getDefaultInstanceForType()
    {
        return pagination.getDefaultInstanceForType();
    }

    @Override
    public Descriptors.Descriptor getDescriptorForType()
    {
        return pagination.getDescriptorForType();
    }

    @Override
    public Map<Descriptors.FieldDescriptor, Object> getAllFields()
    {
        return pagination.getAllFields();
    }

    @Override
    public boolean hasField(Descriptors.FieldDescriptor field)
    {
        return pagination.hasField(field);
    }

    @Override
    public Object getField(Descriptors.FieldDescriptor field)
    {
        return pagination.getField(field);
    }

    @Override
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field)
    {
        return pagination.getRepeatedFieldCount(field);
    }

    @Override
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index)
    {
        return pagination.getRepeatedField(field, index);
    }

    @Override
    public UnknownFieldSet getUnknownFields()
    {
        return pagination.getUnknownFields();
    }

    public int getPageSize()
    {
        return pagination.getPageSize() > 0 ? pagination.getPageSize() : PAGE_SIZE;
    }

    public <T extends AbstractMessage> T getFilter(Class<T> clz, AbstractMessage defaultInstance)
    {
        return searchW.getFilter(clz, defaultInstance);
    }

    public List<QueryWhereParser.FilterProperty> getJsonFilter()
    {
        return searchW.getJsonFilter();
    }

    public Sort getSort()
    {
        Iterator<String> iterator = pagination.getSortParamList().iterator();
        List<Sort.Order> sortOrders = new ArrayList<>();
        while (iterator.hasNext())
        {
            String next = iterator.next();
            StringTokenizer tokenizer = new StringTokenizer(next, ",");
            if (tokenizer.countTokens() == 1)
            {
                sortOrders.add(Sort.Order.desc(tokenizer.nextToken()));
            }
            if (tokenizer.countTokens() == 2)
            {
                String prop = tokenizer.nextToken();
                String direction = tokenizer.nextToken();
                if ("asc".equals(direction))
                    sortOrders.add(Sort.Order.asc(prop));
                else
                    sortOrders.add(Sort.Order.desc(prop));
            }

        }
        return Sort.by(sortOrders);
    }

}
