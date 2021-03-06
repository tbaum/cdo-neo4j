package com.buschmais.cdo.neo4j.test.embedded.relation.composite;

import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.ManyToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToMany;
import com.buschmais.cdo.neo4j.test.embedded.relation.qualified.composite.OneToOne;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedManyToManyRelation;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedOneToManyRelation;
import com.buschmais.cdo.neo4j.test.embedded.relation.typed.composite.TypedOneToOneRelation;

import java.util.List;

import static com.buschmais.cdo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.cdo.neo4j.api.annotation.Relation.Outgoing;

public interface B {

    // Typed relations without qualifier

    @Incoming
    TypedOneToOneRelation getTypedOneToOne();

    @Incoming
    TypedOneToManyRelation getTypedManyToOne();

    @Incoming
    List<TypedManyToManyRelation> getTypedManyToMany();

    // Typed relations with qualifier

    @Outgoing
    @OneToOne
    TypedRelation getOneToOne();

    @Outgoing
    @OneToMany
    TypedRelation getManyToOne();

    @Outgoing
    @ManyToMany
    List<TypedRelation> getManyToMany();

    // Anonymous relations with qualifier

    @Incoming
    @OneToOne
    A getDirectOneToOne();
    void setDirectOneToOne(A a);

    @Incoming
    @OneToMany
    A getDirectManyToOne();

    @Incoming
    @ManyToMany
    List<A> getDirectManyToMany();

}
