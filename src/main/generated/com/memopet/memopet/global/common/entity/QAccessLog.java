package com.memopet.memopet.global.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccessLog is a Querydsl query type for AccessLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccessLog extends EntityPathBase<AccessLog> {

    private static final long serialVersionUID = 1080736012L;

    public static final QAccessLog accessLog = new QAccessLog("accessLog");

    public final StringPath agentClass = createString("agentClass");

    public final StringPath agentName = createString("agentName");

    public final StringPath authorization = createString("authorization");

    public final StringPath clientIp = createString("clientIp");

    public final StringPath deviceClass = createString("deviceClass");

    public final StringPath deviceName = createString("deviceName");

    public final NumberPath<Long> elapsed = createNumber("elapsed", Long.class);

    public final StringPath email = createString("email");

    public final StringPath errorId = createString("errorId");

    public final StringPath host = createString("host");

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final StringPath method = createString("method");

    public final StringPath os = createString("os");

    public final StringPath osName = createString("osName");

    public final StringPath osVersion = createString("osVersion");

    public final StringPath referer = createString("referer");

    public final StringPath request = createString("request");

    public final DateTimePath<java.time.LocalDateTime> requestAt = createDateTime("requestAt", java.time.LocalDateTime.class);

    public final StringPath requestId = createString("requestId");

    public final StringPath response = createString("response");

    public final DateTimePath<java.time.LocalDateTime> responseAt = createDateTime("responseAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath service = createString("service");

    public final StringPath status = createString("status");

    public final StringPath threadId = createString("threadId");

    public final StringPath uri = createString("uri");

    public final StringPath userAgent = createString("userAgent");

    public QAccessLog(String variable) {
        super(AccessLog.class, forVariable(variable));
    }

    public QAccessLog(Path<? extends AccessLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccessLog(PathMetadata metadata) {
        super(AccessLog.class, metadata);
    }

}

