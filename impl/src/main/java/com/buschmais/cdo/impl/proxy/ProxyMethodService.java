package com.buschmais.cdo.impl.proxy;

import com.buschmais.cdo.api.proxy.ProxyMethod;

import java.lang.reflect.Method;

public interface ProxyMethodService <E, M extends ProxyMethod<?>> {

     Object invoke(E element, Object instance, Method method, Object[] args) throws Exception;
}
