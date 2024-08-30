package com.prix.homepage.backend.admin.entity;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLog {
    private int id;
    private String title;
    private Date date;
    private String msFile;
    private String dbFile;
    private String engine;
    private String userName;
    private String result;
}
