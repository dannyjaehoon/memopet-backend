package com.memopet.memopet.domain.pet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemoryImage is a Querydsl query type for MemoryImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemoryImage extends EntityPathBase<MemoryImage> {

    private static final long serialVersionUID = -684171511L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemoryImage memoryImage = new QMemoryImage("memoryImage");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedDate = createDateTime("deletedDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageFormat = createString("imageFormat");

    public final StringPath imageLogicalName = createString("imageLogicalName");

    public final StringPath imagePhysicalName = createString("imagePhysicalName");

    public final StringPath imageSize = createString("imageSize");

    public final StringPath imageUrl = createString("imageUrl");

    public final QMemory memory;

    public QMemoryImage(String variable) {
        this(MemoryImage.class, forVariable(variable), INITS);
    }

    public QMemoryImage(Path<? extends MemoryImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemoryImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemoryImage(PathMetadata metadata, PathInits inits) {
        this(MemoryImage.class, metadata, inits);
    }

    public QMemoryImage(Class<? extends MemoryImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.memory = inits.isInitialized("memory") ? new QMemory(forProperty("memory"), inits.get("memory")) : null;
    }

}

