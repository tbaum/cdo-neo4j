package com.buschmais.cdo.api;

/**
 * An {@Iterable} which allows retrieving a single result.
 *
 * @param <T> The type returned by the {@Iterable}.
 */
public interface ResultIterable<T> extends Iterable<T> {

    /**
     * Return a single result.
     * <p>A {@link CdoException} is thrown if no or more than element is returned by the {@link Iterable}.</p>
     *
     * @return The single result.
     */
    T getSingleResult();

    /**
     * Return <code>true</code> if a result is available.
     *
     * @return <code>true</code> if a result is available.
     */
    boolean hasResult();

    /**
     * Return an result iterator.
     *
     * @return The result iterator.
     */
    ResultIterator<T> iterator();

}
