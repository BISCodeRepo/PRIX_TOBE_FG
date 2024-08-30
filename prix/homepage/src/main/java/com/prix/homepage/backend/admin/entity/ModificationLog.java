package com.prix.homepage.backend.admin.entity;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModificationLog {
    private Date date;
    private String version;
    private String file;
}
