package com.memopet.memopet.domain.oauth2.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class SocialUser {
    public String id;
    public String email;
    public String name;
    public String mobile;
}
