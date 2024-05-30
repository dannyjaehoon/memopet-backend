package com.memopet.memopet.domain.pet.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSpecies is a Querydsl query type for Species
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSpecies extends EntityPathBase<Species> {

    private static final long serialVersionUID = 161225451L;

    public static final QSpecies species = new QSpecies("species");

    public final com.memopet.memopet.global.common.entity.QLastModifiedEntity _super = new com.memopet.memopet.global.common.entity.QLastModifiedEntity(this);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath largeCategory = createString("largeCategory");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath midCategory = createString("midCategory");

    public final StringPath smallCategory = createString("smallCategory");

    public QSpecies(String variable) {
        super(Species.class, forVariable(variable));
    }

    public QSpecies(Path<? extends Species> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSpecies(PathMetadata metadata) {
        super(Species.class, metadata);
    }

}

