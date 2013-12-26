package com.buschmais.cdo.inject;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author tbaum
 * @since 20.02.12
 */
@Target({METHOD, TYPE}) @Retention(RUNTIME) @Inherited @ScopeAnnotation
public @interface Transactional {

    String unit() default "";

    Class<? extends Throwable>[] rollbackOn() default {Throwable.class};

    Class<? extends Throwable>[] noRollbackFor() default {};
}
