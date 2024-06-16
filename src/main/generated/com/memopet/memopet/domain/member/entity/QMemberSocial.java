package com.memopet.memopet.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberSocial is a Querydsl query type for MemberSocial
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberSocial extends EntityPathBase<MemberSocial> {

    private static final long serialVersionUID = -1968367927L;

    public static final QMemberSocial memberSocial = new QMemberSocial("memberSocial");

    public final com.memopet.memopet.global.common.entity.QFirstCreatedEntity _super = new com.memopet.memopet.global.common.entity.QFirstCreatedEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> deletedDate = createDateTime("deletedDate", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastLoginDate = createDateTime("lastLoginDate", java.time.LocalDateTime.class);

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<Integer> loginFailCount = createNumber("loginFailCount", Integer.class);

    public final StringPath memberId = createString("memberId");

    public final EnumPath<MemberStatus> memberStatus = createEnum("memberStatus", MemberStatus.class);

    public final StringPath password = createString("password");

    public final StringPath phoneNum = createString("phoneNum");

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final StringPath roles = createString("roles");

    public final StringPath username = createString("username");

    public QMemberSocial(String variable) {
        super(MemberSocial.class, forVariable(variable));
    }

    public QMemberSocial(Path<? extends MemberSocial> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberSocial(PathMetadata metadata) {
        super(MemberSocial.class, metadata);
    }

}

