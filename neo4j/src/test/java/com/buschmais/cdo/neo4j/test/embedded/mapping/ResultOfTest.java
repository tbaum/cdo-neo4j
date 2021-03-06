package com.buschmais.cdo.neo4j.test.embedded.mapping;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.neo4j.test.embedded.AbstractEmbeddedCdoManagerTest;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.ByValue;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.ByValueUsingImplicitThis;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.E;
import com.buschmais.cdo.neo4j.test.embedded.mapping.composite.F;
import org.junit.Before;
import org.junit.Test;

import static com.buschmais.cdo.api.Query.Result;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ResultOfTest extends AbstractEmbeddedCdoManagerTest {

    private E e;
    private F f1;
    private F f2;

    @Override
    protected Class<?>[] getTypes() {
        return new Class<?>[]{E.class, F.class};
    }

    @Before
    public void createData() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        e = cdoManager.create(E.class);
        f1 = cdoManager.create(F.class);
        f1.setValue("F1");
        e.getRelatedTo().add(f1);
        f2 = cdoManager.create(F.class);
        f2.setValue("F2");
        e.getRelatedTo().add(f2);
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingExplicitQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<ByValue> byValue = e.getResultByValueUsingExplicitQuery("F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void resultUsingReturnType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        Result<ByValue> byValue = e.getResultByValueUsingReturnType("F1");
        assertThat(byValue.getSingleResult().getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingExplicitQuery() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        ByValue byValue = e.getByValueUsingExplicitQuery("F1");
        assertThat(byValue.getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingReturnType() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        ByValue byValue = e.getByValueUsingReturnType("F1");
        assertThat(byValue.getF(), equalTo(f1));
        byValue = e.getByValueUsingReturnType("unknownF");
        assertThat(byValue, equalTo(null));
        cdoManager.currentTransaction().commit();
    }

    @Test
    public void byValueUsingImplicitThis() {
        CdoManager cdoManager = getCdoManager();
        cdoManager.currentTransaction().begin();
        ByValueUsingImplicitThis byValue = e.getByValueUsingImplicitThis("F1");
        assertThat(byValue.getF(), equalTo(f1));
        cdoManager.currentTransaction().commit();
    }
}
