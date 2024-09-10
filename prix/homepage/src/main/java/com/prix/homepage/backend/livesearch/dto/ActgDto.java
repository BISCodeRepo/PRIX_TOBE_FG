package com.prix.homepage.backend.livesearch.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ActgDto {

  private String processName;
  private String prixIndex;
  private String title;
  private String output;
  private String rate;

  private Boolean finished;
  private Boolean failed;

}