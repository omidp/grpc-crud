package com.omid.grpc.service;

import java.util.ArrayList;
import java.util.List;
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

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.jedlab.framework.spring.service.JPARestriction;
import com.jedlab.framework.util.StringUtil;
import com.omid.grpc.annotations.GServerInterceptor;
import com.omid.grpc.domain.PersonEntity;
import com.omid.grpc.server.AuthenticationServerInterceptor;
import com.omid.rpc.Main.Pagination;
import com.omid.rpc.Main.Search;
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
        personService.insert(new PersonEntity(request));
        responseObserver.onNext(request);
        responseObserver.onCompleted();
    }

    @Override
    public void list(Pagination request, StreamObserver<PersonList> responseObserver)
    {

        PersonFilter defaultInstance = com.google.protobuf.Internal.getDefaultInstance(PersonFilter.class);
        ByteString searchFilter = request.getSearch().getFilter();
        PersonFilter filter = PersonFilter.newBuilder().build();
        try
        {
            filter = (PersonFilter) defaultInstance.getParserForType().parseFrom(searchFilter);
        }
        catch (InvalidProtocolBufferException e)
        {
            logger.info("InvalidProtocolBufferException : " + e.getMessage());
        }
        Page<PersonEntity> page = personService.load(PageRequest.of(request.getPage(), request.getPageSize()), PersonEntity.class,
                new PersonRestriction(filter));
        List<Person> persons = page.getContent().stream().map(p -> p.createPersonProto()).collect(Collectors.toList());
        PersonList personList = PersonList.newBuilder().addAllPersons(persons).build();
        responseObserver.onNext(personList);
        responseObserver.onCompleted();
    }

    @Override
    public void count(Search request, StreamObserver<Int64Value> responseObserver)
    {
        // below line cause illegalargument exception why ?
        // PersonFilter filter = (PersonFilter)
        // SerializationUtils.deserialize(request.getFilter().toByteArray());
        PersonFilter defaultInstance = com.google.protobuf.Internal.getDefaultInstance(PersonFilter.class);
        ByteString searchFilter = request.getFilter();
        PersonFilter filter = PersonFilter.newBuilder().build();
        try
        {
            filter = (PersonFilter) defaultInstance.getParserForType().parseFrom(searchFilter);
        }
        catch (InvalidProtocolBufferException e)
        {
            logger.info("InvalidProtocolBufferException : " + e.getMessage());
        }
        Long count = personService.count(PersonEntity.class, new PersonRestriction(filter));
        responseObserver.onNext(Int64Value.newBuilder().setValue(count).build());
        responseObserver.onCompleted();
    }

    @Override
    public void update(Person request, StreamObserver<Person> responseObserver)
    {
        personService.update(new PersonEntity(request));
        responseObserver.onNext(request);
        responseObserver.onCompleted();
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
        responseObserver.onNext(personEntity.createPersonProto());
        responseObserver.onCompleted();
    }

    public static class PersonRestriction implements JPARestriction
    {

        PersonFilter filter;

        public PersonRestriction(PersonFilter filter)
        {
            this.filter = filter;
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
            return builder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }

    }

}