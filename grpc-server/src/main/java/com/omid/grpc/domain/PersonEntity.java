package com.omid.grpc.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jedlab.framework.spring.dao.PO;
import com.jedlab.framework.util.StringUtil;
import com.omid.rpc.Person;
import com.omid.rpc.Person.Builder;

/**
 * @author omidp
 *
 */
@Entity
@Table(name = "persons")
public class PersonEntity extends PO
{

    private String firstName;

    private String lastName;

    private String nationalNo;

    public PersonEntity()
    {
    }

    public PersonEntity(Person person)
    {
        if (person.getId() > 0)
            setId(person.getId());
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.nationalNo = person.getNationalNo();
    }

    public String getNationalNo()
    {
        return nationalNo;
    }

    public void setNationalNo(String nationalNo)
    {
        this.nationalNo = nationalNo;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    @Transient
    public Person createProto()
    {
        Builder builder = Person.newBuilder();
        if (StringUtil.isNotEmpty(firstName))
            builder.setFirstName(firstName);
        if (StringUtil.isNotEmpty(lastName))
            builder.setLastName(lastName);
        if (StringUtil.isNotEmpty(nationalNo))
            builder.setNationalNo(nationalNo);
        if (getId() != null)
            builder.setId(getId());
        return builder.build();
    }

}
