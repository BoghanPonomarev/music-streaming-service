package com.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FileModificationSpecification {

  private Long startTime;
  private Long durationTime;

  private Boolean isRemoveAudio;
  private Boolean isPLayInLoop;
  private Boolean isStream;

  private String generalCodec;
  private String audioCodec;
  private String videoCodec;//Actually should be enum

  private String firstFilePath;
  private String secondFilePath;
  private String resultFilePath;

}
