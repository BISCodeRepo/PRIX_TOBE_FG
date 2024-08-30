package com.prix.homepage.backend.admin.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SoftwareRequest {
    private int id;
    private String date;
    private String name;
    private String affiliation;
    private String title;
    private String email;
    private String instrument;
    private String software;
    private String version;
    private int state;
    private String sentTime;
}
