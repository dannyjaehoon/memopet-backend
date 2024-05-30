package com.memopet.memopet.global.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVerificationStatusEntity is a Querydsl query type for VerificationStatusEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVerificationStatusEntity extends EntityPathBase<VerificationStatusEntity> {

    private static final long serialVersionUID = -1938260764L;

    public static final QVerificationStatusEntity verificationStatusEntity = new QVerificationStatusEntity("verificationStatusEntity");

    public final StringPath authKey = createString("authKey");

    public final DateTimePath<java.time.LocalDateTime> expiredAt = createDateTime("expiredAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QVerificationStatusEntity(String variable) {
        super(VerificationStatusEntity.class, forVariable(variable));
    }

    public QVerificationStatusEntity(Path<? extends VerificationStatusEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVerificationStatusEntity(PathMetadata metadata) {
        super(VerificationStatusEntity.class, metadata);
    }

}

