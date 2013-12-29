package com.buschmais.cdo.inject.test;

import com.buschmais.cdo.inject.GuiceModule;
import com.buschmais.cdo.inject.Transactional;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.PlaceboTransaction;
import org.neo4j.kernel.TopLevelTransaction;

import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.inject.Guice.createInjector;

public class ScopingTest {

    private Injector injector;

    @Before public void setup() {
        injector = createInjector(new GuiceModule());
    }

    @Test(expected = ProvisionException.class)
    public void notScoped() {
        injector.getInstance(A.class).inTx();
    }

    @Test
    public void scoped() {
        final Transaction tx = injector.getInstance(B.class).inTx();
        Assert.assertEquals(TopLevelTransaction.class, tx.getClass());
    }

    @Test
    public void testSimpleScoping() {
        final Transaction[] tx = injector.getInstance(D.class).inTx();
        Assert.assertEquals(TopLevelTransaction.class, tx[0].getClass());
        Assert.assertEquals(PlaceboTransaction.class, tx[1].getClass());
    }

    static class A {
        @Inject Transaction tx;

        Transaction inTx() {
            return tx;
        }
    }

    static class B {
        @Inject Provider<A> aProvider;

        @Transactional Transaction inTx() {
            return aProvider.get().inTx();
        }
    }

    static class C {
        @Inject Provider<A> aProvider;
        @Inject Transaction tx;

        @Transactional Transaction[] inTx() {
            return new Transaction[]{tx, aProvider.get().inTx()};
        }
    }

    static class D {
        @Inject Provider<C> bProvider;

        @Transactional Transaction[] inTx() {
            return bProvider.get().inTx();
        }
    }
}
