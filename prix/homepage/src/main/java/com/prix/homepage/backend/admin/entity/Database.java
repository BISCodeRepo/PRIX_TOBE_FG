package com.prix.homepage.backend.admin.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Database {
    private int id;
    private String name;
    private String file;
}
