package com.prix.homepage.backend.admin.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enzyme {
    private int id;
    private int user_id;
    private String name;
    private String nt_cleave;
    private String ct_cleave;
}