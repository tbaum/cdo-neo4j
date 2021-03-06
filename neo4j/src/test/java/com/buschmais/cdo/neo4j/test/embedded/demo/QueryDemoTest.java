package com.buschmais.cdo.neo4j.test.embedded.demo;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.demo.composite.Group;
import com.buschmais.cdo.neo4j.test.embedded.demo.composite.Person;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class QueryDemoTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{Group.class, Person.class};
    }

    @Test
    public void test() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Group group = cdoManager.create(Group.class);
        Person person1 = cdoManager.create(Person.class);
        person1.setName("Peter");
        group.getMembers().add(person1);
        Person person2 = cdoManager.create(Person.class);
        person2.setName("Dirk");
        group.getMembers().add(person2);
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        Group.MemberByName memberByName = (Group.MemberByName) cdoManager.createQuery(Group.MemberByName.class).withParameter("this", group).withParameter("name", "Peter").execute().getSingleResult();
        Person peter = memberByName.getMember();
        assertThat(peter, equalTo(person1));
        cdoManager.currentTransaction().commit();
        cdoManager.currentTransaction().begin();
        memberByName = group.getMemberByName("Peter");
        peter = memberByName.getMember();
        assertThat(peter, equalTo(person1));
        cdoManager.currentTransaction().commit();
    }
}
