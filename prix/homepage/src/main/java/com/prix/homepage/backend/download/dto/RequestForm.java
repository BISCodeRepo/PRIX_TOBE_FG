package com.prix.homepage.backend.download.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestForm {
    private String software;
    private String agreement;
    private String name;
    private String affiliation;
    private String title;
    private String email;
    private String instrument;
}
