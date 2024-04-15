package com.memopet.memopet.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meta extends LastModifiedEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String tableName;

    @Column(nullable = false)
    private String column_name;

    @Column(nullable = false)
    private int metaData;

    @Column(nullable = false)
    private String metaDataName;


}
