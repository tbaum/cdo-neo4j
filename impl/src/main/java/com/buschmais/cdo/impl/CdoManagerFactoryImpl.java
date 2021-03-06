package com.buschmais.cdo.impl;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.TransactionAttribute;
import com.buschmais.cdo.impl.reflection.ClassHelper;
import com.buschmais.cdo.impl.interceptor.InterceptorFactory;
import com.buschmais.cdo.api.bootstrap.CdoUnit;
import com.buschmais.cdo.impl.cache.CacheSynchronization;
import com.buschmais.cdo.impl.validation.InstanceValidator;
import com.buschmais.cdo.impl.validation.ValidatorSynchronization;
import com.buschmais.cdo.impl.cache.TransactionalCache;
import com.buschmais.cdo.impl.metadata.MetadataProviderImpl;
import com.buschmais.cdo.spi.bootstrap.CdoDatastoreProvider;
import com.buschmais.cdo.spi.datastore.Datastore;
import com.buschmais.cdo.spi.datastore.DatastoreSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

public class CdoManagerFactoryImpl implements CdoManagerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdoManagerFactoryImpl.class);

    private CdoUnit cdoUnit;
    private MetadataProvider metadataProvider;
    private ClassLoader classLoader;
    private Datastore<?, ?, ?> datastore;
    private ValidatorFactory validatorFactory;
    private TransactionAttribute defaultTransactionAttribute;

    public CdoManagerFactoryImpl(CdoUnit cdoUnit) {
        this.cdoUnit = cdoUnit;
        Class<?> providerType = cdoUnit.getProvider();
        if (providerType == null) {
            throw new CdoException("No provider specified for CDO unit '" + cdoUnit.getName() + "'.");
        }
        if (!CdoDatastoreProvider.class.isAssignableFrom(providerType)) {
            throw new CdoException(providerType.getName() + " specified as CDO provider must implement " + CdoDatastoreProvider.class.getName());
        }
        CdoDatastoreProvider cdoDatastoreProvider = CdoDatastoreProvider.class.cast(ClassHelper.newInstance(providerType));
        datastore = cdoDatastoreProvider.createDatastore(cdoUnit);
        this.defaultTransactionAttribute = cdoUnit.getDefaultTransactionAttribute();
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        classLoader = contextClassLoader != null ? contextClassLoader : cdoUnit.getClass().getClassLoader();
        LOGGER.info("Using class loader '{}'.", contextClassLoader.toString());
        classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return contextClassLoader.loadClass(name);
            }
        };
        metadataProvider = new MetadataProviderImpl(cdoUnit.getTypes(), datastore);
        try {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        } catch (ValidationException e) {
            LOGGER.debug("Cannot find validation provider.", e);
            LOGGER.info("No JSR 303 Bean Validation provider available.");
        }
        datastore.init(metadataProvider.getRegisteredMetadata());
    }

    @Override
    public CdoManager createCdoManager() {
        DatastoreSession datastoreSession = datastore.createSession();
        TransactionalCache<?> cache = new TransactionalCache();
        InstanceValidator instanceValidator = new InstanceValidator(validatorFactory, cache);
        CdoTransactionImpl cdoTransaction = new CdoTransactionImpl(datastoreSession.getDatastoreTransaction());
        InterceptorFactory interceptorFactory = new InterceptorFactory(cdoTransaction, defaultTransactionAttribute);
        InstanceManager instanceManager = new InstanceManager(metadataProvider, datastoreSession, classLoader, cdoTransaction, cache, interceptorFactory);
        // Register default synchronizations.
        cdoTransaction.registerDefaultSynchronization(new ValidatorSynchronization(instanceValidator));
        cdoTransaction.registerDefaultSynchronization(new CacheSynchronization(instanceManager, cache, datastoreSession));
        return interceptorFactory.addInterceptor(new CdoManagerImpl(metadataProvider, cdoTransaction, cache, datastoreSession, instanceManager, interceptorFactory, instanceValidator));
    }

    @Override
    public void close() {
        datastore.close();
    }

    public CdoUnit getCdoUnit() {
        return cdoUnit;
    }
}
