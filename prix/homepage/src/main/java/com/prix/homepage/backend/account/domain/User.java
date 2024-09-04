package com.prix.homepage.backend.account.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {


    private int id;

    private int userid;
    private String email;
    private String password;
    private String name;
    private String affiliation;
    private int level;
}