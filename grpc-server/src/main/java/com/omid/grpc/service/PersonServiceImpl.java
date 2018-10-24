package com.omid.grpc.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.jedlab.framework.db.QueryMapper;
import com.jedlab.framework.spring.rest.QueryWhereParser;
import com.jedlab.framework.spring.security.AuthenticationUtil;
import com.jedlab.framework.spring.service.JPARestriction;
import com.jedlab.framework.util.StringUtil;
import com.omid.grpc.annotations.GServerInterceptor;
import com.omid.grpc.dao.PersonDao;
import com.omid.grpc.domain.PersonEntity;
import com.omid.grpc.server.AuthenticationServerInterceptor;
import com.omid.grpc.server.wrapper.PaginationWrapper;
import com.omid.grpc.server.wrapper.SearchWrapper;
import com.omid.rpc.Main;
import com.omid.rpc.Person;
import com.omid.rpc.PersonFilter;
import com.omid.rpc.PersonList;
import com.omid.rpc.PersonServiceGrpc.PersonServiceImplBase;

import io.grpc.stub.StreamObserver;

/**
 * @author omidp
 *
 */
@Component
@GServerInterceptor(AuthenticationServerInterceptor.class)
public class PersonServiceImpl extends PersonServiceImplBase
{

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger logger = Logger.getLogger(PersonServiceImpl.class.getName());

    PersonService personService;

    @Autowired
    public PersonServiceImpl(PersonService personService)
    {
        this.personService = personService;
    }

    @Override
    public void create(Person request, StreamObserver<Person> responseObserver)
    {
        PersonEntity personEntity = new PersonEntity(request);
        personService.insert(personEntity);
        Person build = request.toBuilder().setId(personEntity.getId()).build();
        responseObserver.onNext(build);
        responseObserver.onCompleted();
    }

    @Override
    public void list(Main.Pagination request, StreamObserver<PersonList> responseObserver)
    {        
        PaginationWrapper pw = new PaginationWrapper(request);
        PersonFilter filter = pw.getFilter(PersonFilter.class, PersonFilter.newBuilder().build());
        Page<PersonEntity> page = personService.load(PageRequest.of(request.getPage(), pw.getPageSize()), PersonEntity.class,
                new PersonRestriction(filter, pw.getJsonFilter()), pw.getSort());
        List<Person> persons = page.getContent().stream().map(PersonEntity::createProto).collect(Collectors.toList());
        PersonList personList = PersonList.newBuilder().addAllResultList(persons)
                .setResultCount(Int64Value.newBuilder().setValue(page.getTotalElements()).build()).build();
        responseObserver.onNext(personList);
        responseObserver.onCompleted();
    }

    @Override
    public void count(Main.Search request, StreamObserver<Int64Value> responseObserver)
    {
        SearchWrapper sw = new SearchWrapper(request);
        Long count = personService.count(PersonEntity.class,
                new PersonRestriction(sw.getFilter(PersonFilter.class, PersonFilter.newBuilder().build()), sw.getJsonFilter()));
        responseObserver.onNext(Int64Value.newBuilder().setValue(count).build());
        responseObserver.onCompleted();
    }

    @Override
    public void update(Main.UpdateValue request, StreamObserver<Person> responseObserver)
    {

        Optional<PersonEntity> op = ((PersonDao) personService.getDao()).findById(request.getId());
        if (op.isPresent())
        {
            try
            {
                PersonEntity pe = MAPPER.readerForUpdating(op.get()).readValue(request.getJsonValue());
                personService.update(pe);
                responseObserver.onNext(pe.createProto());
                responseObserver.onCompleted();
            }
            catch (IOException e)
            {
                responseObserver.onError(e);
            }

        }
        else
        {
            responseObserver.onError(new ResponseException());
        }

    }

    @Override
    public void delete(Int64Value request, StreamObserver<Empty> responseObserver)
    {
        personService.delete(request.getValue());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void findById(Int64Value request, StreamObserver<Person> responseObserver)
    {
        PersonEntity personEntity = personService.findById(PersonEntity.class, request.getValue());
        responseObserver.onNext(personEntity.createProto());
        responseObserver.onCompleted();
    }

    @Override
    public void onClientStream(Empty request, StreamObserver<StringValue> responseObserver)
    {
        responseObserver.onNext(StringValue.newBuilder().setValue("stream1").build());
        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            //DO NOTHING
        }
        responseObserver.onNext(StringValue.newBuilder().setValue("stream2").build());
        // never close this for websocket
    }

    public static class PersonRestriction implements JPARestriction
    {

        PersonFilter filter;
        List<QueryWhereParser.FilterProperty> jsonFilter;

        public PersonRestriction(PersonFilter filter, List<QueryWhereParser.FilterProperty> jsonFilter)
        {
            this.filter = filter;
            this.jsonFilter = jsonFilter;
        }

        @Override
        public Specification countSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root)
        {
            return (rootEntity, query, criteriaBuilder) -> {
                return applyFilter(root, criteria, builder);
            };
        }

        @Override
        public Specification listSpec(CriteriaBuilder builder, CriteriaQuery criteria, Root root)
        {
            return (rootEntity, query, criteriaBuilder) -> {
                return applyFilter(root, criteria, builder);
            };
        }

        private Predicate applyFilter(Root root, CriteriaQuery criteria, CriteriaBuilder builder)
        {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            if (StringUtil.isNotEmpty(filter.getNationalNo()))
            {
                predicateList.add(builder.equal(root.get("nationalNo"), filter.getNationalNo()));
            }
            predicateList.addAll(QueryMapper.filterMap(jsonFilter, builder, criteria, root, PersonEntity.class));
            return builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }

    }

}