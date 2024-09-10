package com.prix.homepage.backend.livesearch.pojo;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchLog {
    private Integer userId;
    private String title;
    private LocalDate date;
}
