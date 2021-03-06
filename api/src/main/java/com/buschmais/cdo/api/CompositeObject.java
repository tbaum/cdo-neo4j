package com.buschmais.cdo.api;

/**
 * Defines an interface which is transparently implemented by all composite instances created by the {@link CdoManager}.
 */
public interface CompositeObject {

    /**
     * Cast the instance to a specific type.
     *
     * @param type The type.
     * @param <T>  The type.
     * @return The instance.
     */
    <T> T as(Class<T> type);

}
