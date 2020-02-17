package com.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FileAnalysisSpecification {

  private String filePath;

  private Boolean isGetMetadata;

}
