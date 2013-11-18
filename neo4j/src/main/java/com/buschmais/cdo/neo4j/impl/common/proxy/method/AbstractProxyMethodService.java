package com.buschmais.cdo.neo4j.impl.common.proxy.method;

import com.buschmais.cdo.api.CdoException;
import com.buschmais.cdo.neo4j.api.proxy.ProxyMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AbstractProxyMethodService<E, M extends ProxyMethod<?>> {

    private final Map<Method, ProxyMethod<E>> proxyMethods = new HashMap<>();

    public Object invoke(E element, Object instance, Method method, Object[] args) {
        ProxyMethod<E> proxyMethod = proxyMethods.get(method);
        if (proxyMethod == null) {
            throw new CdoException("Cannot find proxy for method " + method.getName());
        }
        return proxyMethod.invoke(element, instance, args);
    }

    protected void addMethod(ProxyMethod<E> proxyMethod, Class<?> type, String name, Class<?>... argumentTypes) {
        Method method;
        try {
            method = type.getDeclaredMethod(name, argumentTypes);
        } catch (NoSuchMethodException e) {
            throw new CdoException("Cannot resolve method '" + name + "' (" + Arrays.asList(argumentTypes) + ")");
        }
        addProxyMethod(proxyMethod, method);
    }

    protected void addProxyMethod(ProxyMethod<E> proxyMethod, Method method) {
        if (method != null) {
            proxyMethods.put(method, proxyMethod);
        }
    }
}