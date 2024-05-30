package com.memopet.memopet.global.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMeta is a Querydsl query type for Meta
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeta extends EntityPathBase<Meta> {

    private static final long serialVersionUID = 982825145L;

    public static final QMeta meta = new QMeta("meta");

    public final QLastModifiedEntity _super = new QLastModifiedEntity(this);

    public final StringPath column_name = createString("column_name");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Integer> metaData = createNumber("metaData", Integer.class);

    public final StringPath metaDataName = createString("metaDataName");

    public final StringPath tableName = createString("tableName");

    public QMeta(String variable) {
        super(Meta.class, forVariable(variable));
    }

    public QMeta(Path<? extends Meta> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMeta(PathMetadata metadata) {
        super(Meta.class, metadata);
    }

}

