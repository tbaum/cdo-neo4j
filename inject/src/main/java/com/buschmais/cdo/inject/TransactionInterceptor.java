package com.buschmais.cdo.inject;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoManagerFactory;
import com.buschmais.cdo.api.CdoTransaction;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.inject.name.Names.named;

/**
 * @author tbaum
 * @since 04.10.2013
 */
public class TransactionInterceptor implements MethodInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionInterceptor.class);
    @Inject private final Provider<TransactionScope> transactionScopeProvider = null;
    @Inject private final Injector injector = null;

    @Override public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Transactional annotation = methodInvocation.getMethod().getAnnotation(Transactional.class);
        CdoManagerFactory cdoManagerFactory = getCdoManagerFactory(annotation);
        TransactionScope transactionScope = transactionScopeProvider.get();

        try (CdoManager cdoManager = cdoManagerFactory.createCdoManager()) {
            CdoTransaction transaction = cdoManager.currentTransaction();

            try (TransactionScope ignored = transactionScope.enter(transaction, cdoManager)) {

                final boolean createdTxHere = !transaction.isActive();

                if (createdTxHere) transaction.begin();

                try {
                    final Object result = methodInvocation.proceed();
                    transactionScope.success();
                    return result;
                } catch (Throwable throwable) {
                    final Class<? extends Throwable> throwableClass = throwable.getClass();
                    if (noRollback(annotation, throwableClass)) {
                        LOG.debug("marking transaction success (catched exception {})", throwableClass);
                        transactionScope.success();
                    } else {
                        transactionScope.failed();
                    }
                    throw throwable;
                } finally {
                    if (createdTxHere) {
                        if (transactionScope.isFailed()) {
                            transaction.rollback();
                        } else {
                            transaction.commit();
                        }
                    }
                }
            }
        }
    }

    private CdoManagerFactory getCdoManagerFactory(Transactional annotation) {
        final Key<CdoManagerFactory> key = annotation.unit().isEmpty()
                ? Key.get(CdoManagerFactory.class)
                : Key.get(CdoManagerFactory.class, named(annotation.unit()));

        return injector.getInstance(key);
    }

    private boolean noRollback(Transactional a, Class<? extends Throwable> throwable) {
        return a != null && (isAssignable(throwable, a.noRollbackFor()) || !isAssignable(throwable, a.rollbackOn()));
    }

    private boolean isAssignable(Class subClass, Class... classes) {
        for (Class<?> aClass : classes) {
            if (aClass.isAssignableFrom(subClass)) {
                return true;
            }
        }
        return false;
    }
}
