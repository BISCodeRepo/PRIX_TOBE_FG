package com.prix.homepage.backend.history.domain;

import lombok.Data;

@Data
public class SearchLog {
    private String title;
    private String date;
    private int msFile;
    private int dbFile;
    private int result;
    private String engine;
}