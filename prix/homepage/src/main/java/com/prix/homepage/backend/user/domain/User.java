package com.prix.homepage.backend.user.domain;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {


    private Long id;

    private Integer userid;
    private String email;
    private String password;
    private String name;
    private String affiliation;
    private int level;
}