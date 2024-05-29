package com.prix.homepage.backend.livesearch.pojo;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Modification {
    private Integer id;

    private Integer userId;

    private String name;

    private String fullname;

    private Integer classNum;
    //@Result(property = "stringClass", column = "class")
    private String stringClass;

    private Double massDiff;

    private Double avgMassDiff;

    private String residue;

    private String position;
}
