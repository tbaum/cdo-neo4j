package com.buschmais.cdo.impl.proxy.query.object;

import com.buschmais.cdo.impl.proxy.query.RowProxyMethod;

import java.util.Map;

public class HashCodeMethod implements RowProxyMethod {

    @Override
    public Object invoke(Map<String, Object> entity, Object instance, Object[] args) {
        return entity.hashCode();
    }
}
