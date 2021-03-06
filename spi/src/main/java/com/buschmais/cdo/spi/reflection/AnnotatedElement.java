package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;

/**
 * Defines a annotated element which provides information about present annotations identified by the annotation types themselves or meta annotations.
 *
 * @param <AE> The annotated element type.
 */
public interface AnnotatedElement<AE extends java.lang.reflect.AnnotatedElement> {

    /**
     * Return the annotated element.
     *
     * @return The annotated element.
     */
    AE getAnnotatedElement();

    /**
     * Return an annotation identified by its type.
     *
     * @param annotation The annotation type.
     * @param <T>        The annotationt type.
     * @return The annotation or <code>null</code>.
     */
    <T extends Annotation> T getAnnotation(Class<T> annotation);

    /**
     * Return an annotation identified by a meta annotation type.
     *
     * @param metaAnnotation The meta annotation type.
     * @param <T>            The meta annotation type.
     * @return The annotation or <code>null</code>.
     */
    <T extends Annotation, M extends Annotation> T getByMetaAnnotation(Class<M> metaAnnotation);
}
