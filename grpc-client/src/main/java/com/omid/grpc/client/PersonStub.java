package com.omid.grpc.client;

import com.omid.rpc.Person;
import com.omid.rpc.PersonFilter;
import com.omid.rpc.PersonList;

public interface PersonStub
{
    public void createPerson(String firstName, String lastName, String nationalNo);
    public PersonList personList(int page, int pageSize, PersonFilter filter);
    public Number personCount(PersonFilter filter);
    public void updatePerson(Long id, String firstName, String lastName, String nationalNo);
    public void delete(Long id);
    public Person findById(Long id);
}
