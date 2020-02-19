package com.service.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamUnit {

  private Long id;

  private String filePath;
  private Double duration;

}
