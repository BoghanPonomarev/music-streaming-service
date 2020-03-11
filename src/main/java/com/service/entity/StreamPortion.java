package com.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamPortion {

  private Long id;
  private String streamName;

  private String filePath;
  private Double duration;
  private boolean isFirstSegmentPortion;

}
