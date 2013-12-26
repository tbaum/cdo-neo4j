package com.buschmais.cdo.inject;

import com.buschmais.cdo.api.CdoManager;
import com.buschmais.cdo.api.CdoTransaction;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.HashMap;
import java.util.Map;

public class TransactionScope implements Scope, AutoCloseable {

    public static final TransactionScope TRANSACTIONAL = new TransactionScope();
    private final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<>();
    private final ThreadLocal<Boolean> success = new ThreadLocal<>();
    private final Key<CdoTransaction> CDO_TRANSACTION = Key.get(CdoTransaction.class);
    private final Key<CdoManager> CDO_MANAGER = Key.get(CdoManager.class);

    private TransactionScope() {
    }

    public static <T> Provider<T> defaultProvider() {
        return new Provider<T>() {
            public T get() {
                throw new IllegalStateException("If you got here then it means that your code asked for scoped " +
                        "object which should have been explicitly initialized in this scope");
            }
        };
    }

    public TransactionScope enter(CdoTransaction transaction, CdoManager manager) {
        if (values.get() != null) {
            throw new IllegalStateException("Manager was already set in this scope.");
        }

        final HashMap<Key<?>, Object> map = new HashMap<>();
        map.put(CDO_TRANSACTION, transaction);
        map.put(CDO_MANAGER, manager);

        values.set(map);
        success.set(null);
        return this;
    }

    public void exit() {
        if (values.get() == null) {
            throw new IllegalStateException("No scoping block in progress");
        }

        values.set(null);
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return scopedObjects;
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

                @SuppressWarnings("unchecked")
                T current = (T) scopedObjects.get(key);

                if (current == null) {
                    current = unscoped.get();
                    scopedObjects.put(key, current);
                }
                return current;
            }
        };
    }

    public void success() {
        if (success.get() == Boolean.FALSE)
            throw new IllegalStateException("unable to mark already failed transaction successful");

        success.set(true);
    }

    public void failed() {
        success.set(false);
    }

    public boolean isFailed() {
        return success.get() != Boolean.TRUE;
    }

    @Override public void close() {
        exit();
    }
}
