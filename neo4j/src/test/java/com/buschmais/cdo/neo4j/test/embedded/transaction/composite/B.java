package com.buschmais.cdo.neo4j.test.embedded.transaction.composite;

import com.buschmais.cdo.neo4j.api.annotation.Label;

@Label("B")
public interface B {

    int getValue();

    void setValue(int value);

}
