package com.omid.grpc.client;

import com.omid.rpc.Person;
import com.omid.rpc.PersonFilter;
import com.omid.rpc.PersonList;

public interface PersonStub
{
    public Person findById(Long id);
    public void delete(Long id);
    public void updatePerson(Long id);
    public Number personCount(PersonFilter filter, String jsonFilter);
    public PersonList personList(int page, int pageSize, PersonFilter filter, String jsonFilter);
    public void createPerson(String firstName, String lastName, String nationalNo);
}
