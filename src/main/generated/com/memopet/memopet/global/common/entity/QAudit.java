package com.memopet.memopet.global.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAudit is a Querydsl query type for Audit
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAudit extends EntityPathBase<Audit> {

    private static final long serialVersionUID = 392187815L;

    public static final QAudit audit = new QAudit("audit");

    public final StringPath cnaf = createString("cnaf");

    public final StringPath cnbf = createString("cnbf");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final StringPath modifier = createString("modifier");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public QAudit(String variable) {
        super(Audit.class, forVariable(variable));
    }

    public QAudit(Path<? extends Audit> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAudit(PathMetadata metadata) {
        super(Audit.class, metadata);
    }

}

