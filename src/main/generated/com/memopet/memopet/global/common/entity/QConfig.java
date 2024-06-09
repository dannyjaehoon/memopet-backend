package com.memopet.memopet.global.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QConfig is a Querydsl query type for Config
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConfig extends EntityPathBase<Config> {

    private static final long serialVersionUID = -675067658L;

    public static final QConfig config = new QConfig("config");

    public final StringPath code = createString("code");

    public final StringPath description = createString("description");

    public final StringPath val = createString("val");

    public QConfig(String variable) {
        super(Config.class, forVariable(variable));
    }

    public QConfig(Path<? extends Config> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConfig(PathMetadata metadata) {
        super(Config.class, metadata);
    }

}

