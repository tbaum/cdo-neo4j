package com.buschmais.cdo.inject;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.CdoTransaction;
import com.buschmais.cdo.api.bootstrap.Cdo;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Singleton;

import static com.buschmais.cdo.inject.TransactionScope.TRANSACTIONAL;
import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

public class GuiceModule extends AbstractModule {

    private final String defaultUnit;

    public GuiceModule() {
        this("default");
    }

    public GuiceModule(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    @Override
    protected void configure() {
        TransactionInterceptor tx = new TransactionInterceptor();
        requestInjection(tx);
        bindInterceptor(annotatedWith(Transactional.class), any(), tx);
        bindInterceptor(any(), annotatedWith(Transactional.class), tx);

        bindScope(Transactional.class, TRANSACTIONAL);
        bind(TransactionScope.class).toInstance(TRANSACTIONAL);
        bind(CdoTransaction.class).toProvider(TransactionScope.<CdoTransaction>defaultProvider()).in(TRANSACTIONAL);
        bind(CdoManager.class).toProvider(TransactionScope.<CdoManager>defaultProvider()).in(TRANSACTIONAL);
    }

    @Provides @Singleton CdoManagerFactory cdoManagerFactory() {
        return Cdo.createCdoManagerFactory(defaultUnit);
    }
}
