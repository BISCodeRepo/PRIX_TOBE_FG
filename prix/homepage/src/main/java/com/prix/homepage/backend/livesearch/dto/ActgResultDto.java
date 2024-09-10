package com.prix.homepage.backend.livesearch.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ActgResultDto {

    String userName;
    String title;

    String proteinDB;
    String proteinOption;

    String VSGOption;
    String variantSpliceGraphDB;

    String method;
    String IL;
    String index;
    LocalDate date;
}
