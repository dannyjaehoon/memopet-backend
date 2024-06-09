package com.memopet.memopet.global.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    public String code;

    private String val;

    private String description;

}