package com.buschmais.cdo.neo4j.test.embedded.inheritance;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.inheritance.composite.A;
import com.buschmais.cdo.neo4j.test.embedded.inheritance.composite.D;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class AnonymousSubTypeTest extends AbstractEmbeddedCdoManagerTest {

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{D.class};
    }

    @Test
    public void anonymousSubType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        D b = cdoManager.create(D.class);
        b.setIndex("1");
        cdoManager.currentTransaction().commit();
        closeCdoManager();
        cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        A a = cdoManager.find(A.class, "1").iterator().next();
        assertThat(a.getIndex(), equalTo("1"));
        cdoManager.currentTransaction().commit();
    }

}
