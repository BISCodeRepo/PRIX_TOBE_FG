package com.prix.homepage.backend.livesearch.pojo.dbond;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PxData {
    private String type;
    private String name;
    private byte[] content;
}
