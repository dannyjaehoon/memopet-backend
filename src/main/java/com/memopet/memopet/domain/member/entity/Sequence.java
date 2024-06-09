package com.memopet.memopet.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sequence {

    @Id
    private String name;
    private long value;
}
