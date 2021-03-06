package com.buschmais.cdo.api;

import javax.validation.ConstraintViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import static com.buschmais.cdo.api.Query.Result.CompositeRowObject;
import static com.buschmais.cdo.api.TransactionAttribute.NOT_SUPPORTED;

/**
 * Defines methods to manage the lifecycle of property instances, query execution and transaction management.
 */
public interface CdoManager extends AutoCloseable {

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Transaction {
        TransactionAttribute value();
    }

    @Transaction(NOT_SUPPORTED)
    CdoTransaction currentTransaction();

    Set<ConstraintViolation<Object>> validate();

    /**
     * Find all property instances according to the given type and value (e.g. from an index).
     *
     * @param <T>   The property type.
     * @param type  The interface of the property type.
     * @param value The value.
     * @return An {@Iterable} returning the property instance.
     */
    <T> ResultIterable<T> find(Class<T> type, Object value);

    /**
     * Create a new {@CompositeObject} instance.
     *
     * @param type  The interface the property type shall implement.
     * @param types Additional interfaces the property type shall implement.
     * @return The {@link CompositeObject} instance.
     */
    CompositeObject create(Class type, Class<?>... types);

    /**
     * Create a new property instance.
     *
     * @param <T>  The expected return type. Note that it must be assignable to at least one of the interfaces specified for the types.
     * @param type The interface the property type shall implement.
     * @return The property instance.
     */
    <T> T create(Class<T> type);

    /**
     * Migrates the type of a property instance to the given target types and returns it. The original instance will not be usable anymore after migration.
     *
     * @param <T>         The property type.
     * @param <M>         The migrated property type. Note that it be assignable to at least one of the interfaces specified for types.
     * @param instance    The instance.
     * @param targetType  The target interface  which shall be implemented by the migrated instance.
     * @param targetTypes More target interfaces which shall be implemented by the migrated instance.
     * @return The migrated instance.
     */
    <T, M> CompositeObject migrate(T instance, Class<M> targetType, Class<?>... targetTypes);

    /**
     * Migrates the type of a property instance to the given target type and returns it. The original instance will not be usable anymore after migration.
     *
     * @param <T>        The property type.
     * @param <M>        The migrated property type. Note that it be assignable to at least one of the interfaces specified for types.
     * @param instance   The instance.
     * @param targetType The target interface  which shall be implemented by the migrated instance.
     * @return The migrated instance.
     */
    <T, M> M migrate(T instance, Class<M> targetType);

    /**
     * Migrates the type of a property instance to the given target and returns it. The original instance will not be usable anymore after migration.
     *
     * @param <T>               The property type.
     * @param <M>               The migrated property type. Note that it be assignable to at least one of the interfaces specified for types.
     * @param instance          The instance.
     * @param migrationStrategy The {@link CdoManager.MigrationStrategy} to be used to migrate data (e.g. properties) to the new type.
     * @param targetType        The target interface which shall be implemented by the migrated instance.
     * @param targetTypes       More target interfaces which shall be implemented by the migrated instance.
     * @return The migrated instance.
     */
    <T, M> CompositeObject migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType, Class<?>... targetTypes);

    /**
     * Migrates the type of a property instance to the given target and returns it. The original instance will not be usable anymore after migration.
     *
     * @param <T>               The property type.
     * @param <M>               The migrated property type. Note that it be assignable to at least one of the interfaces specified for types.
     * @param instance          The instance.
     * @param migrationStrategy The {@link CdoManager.MigrationStrategy} to be used to migrate data (e.g. properties) to the target type.
     * @param targetType        The target interface which shall be implemented by the migrated instance.
     * @return The migrated instance.
     */
    <T, M> M migrate(T instance, MigrationStrategy<T, M> migrationStrategy, Class<M> targetType);

    /**
     * Deletes a property instance.
     *
     * @param <T>      The property type.
     * @param instance The instance.
     */
    <T> void delete(T instance);

    /**
     * Creates a {@link Query}.
     *
     * @param query The query expression.
     * @return The {@link Query}.
     */
    Query<CompositeRowObject> createQuery(String query);

    /**
     * Creates a typed {@link Query}.
     *
     * @param <T>   The type to be returned.
     * @param query The query expression.
     * @param type  The type to be returned.
     * @return The {@link Query}.
     */
    <T> Query<T> createQuery(String query, Class<T> type);

    /**
     * Creates a typed {@link Query}.
     *
     * @param query The query expression.
     * @param type  The type to be returned.
     * @param types The types to be returned.
     * @return The {@link Query}.
     */
    Query<CompositeRowObject> createQuery(String query, Class<?> type, Class<?>... types);

    /**
     * Creates a typed {@link Query}.
     *
     * @param <T>   The type to be returned.
     * @param query The query type.
     * @return The {@link Query}.
     */
    <T> Query<T> createQuery(Class<T> query);

    /**
     * Creates a typed {@link Query}.
     *
     * @param query The query type.
     * @param types The additional types to be returned.
     * @return The {@link Query}.
     */
    Query<CompositeRowObject> createQuery(Class<?> query, Class<?>... types);

    /**
     * Close the {@CdoManager}.
     */
    @Transaction(NOT_SUPPORTED)
    void close();

    /**
     * Return the underlying datastore session.
     *
     * @param sessionType The expected session type.
     * @param <DS>        The expected session type.
     * @return The expected session type.
     */
    <DS> DS getDatastoreSession(Class<DS> sessionType);

    /**
     * Flushes all pending changes to the datastore.
     */
    void flush();

    /**
     * Defines the interface of strategies for migration between different composite object types.
     *
     * @param <T> The instance type.
     * @param <M> The target instance type.
     */
    interface MigrationStrategy<T, M> {
        /**
         * Migrate an instance of a type to instance of another type.
         *
         * @param instance The instance.
         * @param target   The target instance.
         */
        void migrate(T instance, M target);
    }
}
