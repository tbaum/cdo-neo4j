package com.buschmais.cdo.spi.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AbstractBeanMethod implements BeanMethod {

    private Method method;

    protected AbstractBeanMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return getMethod().getAnnotation(type);
    }

    public <T extends Annotation, M extends Annotation> T getByMetaAnnotation(Class<M> type) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(type)) {
                return (T) annotation;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "AbstractBeanMethod{" +
                "method=" + method.getDeclaringClass() + "#" + method +
                '}';
    }
}