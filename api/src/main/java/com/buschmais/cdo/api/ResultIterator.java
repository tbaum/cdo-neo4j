package com.buschmais.cdo.api;

import java.io.Closeable;
import java.util.Iterator;

/**
 * An {@Iterator} which extends {@link AutoCloseable} and {@link Closeable}.
 *
 * @param <T> The type returned by the {@Iterator}.
 */
public interface ResultIterator<T> extends Iterator<T>, AutoCloseable, Closeable {

    @Override
    void close();
}
