package com.memopet.memopet.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSequence is a Querydsl query type for Sequence
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSequence extends EntityPathBase<Sequence> {

    private static final long serialVersionUID = -460768029L;

    public static final QSequence sequence = new QSequence("sequence");

    public final StringPath name = createString("name");

    public final NumberPath<Long> value = createNumber("value", Long.class);

    public QSequence(String variable) {
        super(Sequence.class, forVariable(variable));
    }

    public QSequence(Path<? extends Sequence> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSequence(PathMetadata metadata) {
        super(Sequence.class, metadata);
    }

}

