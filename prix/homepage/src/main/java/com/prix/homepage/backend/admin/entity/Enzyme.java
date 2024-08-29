package com.prix.homepage.backend.admin.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enzyme {
    private int id;
    private int userId;
    private String name;
    private String ntCleave;
    private String ctCleave;
}