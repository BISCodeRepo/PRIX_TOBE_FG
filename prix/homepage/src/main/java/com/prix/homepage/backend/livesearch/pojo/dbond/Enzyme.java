package com.prix.homepage.backend.livesearch.pojo.dbond;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Enzyme {

    private Long id;
    private String name;
    private String userId;
    private String ntCleave;
    private String ctCleave;
}
