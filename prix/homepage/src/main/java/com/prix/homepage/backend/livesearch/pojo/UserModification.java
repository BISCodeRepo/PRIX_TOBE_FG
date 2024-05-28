package com.prix.homepage.backend.livesearch.pojo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModification {
    private Integer userId;
    private Integer modId;
    private Boolean variable;
    private Boolean engine;
}
