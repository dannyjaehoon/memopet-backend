package com.memopet.memopet.domain.pet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlocked is a Querydsl query type for Blocked
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlocked extends EntityPathBase<Blocked> {

    private static final long serialVersionUID = -2146714725L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlocked blocked = new QBlocked("blocked");

    public final QPet blockedPet;

    public final NumberPath<Long> blockerPetId = createNumber("blockerPetId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBlocked(String variable) {
        this(Blocked.class, forVariable(variable), INITS);
    }

    public QBlocked(Path<? extends Blocked> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlocked(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlocked(PathMetadata metadata, PathInits inits) {
        this(Blocked.class, metadata, inits);
    }

    public QBlocked(Class<? extends Blocked> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blockedPet = inits.isInitialized("blockedPet") ? new QPet(forProperty("blockedPet"), inits.get("blockedPet")) : null;
    }

}

