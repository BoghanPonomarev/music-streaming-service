package com.service.stream.compile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StreamCompileContext {

  private Integer iteration;
  private String streamName;
  private boolean isOnlyTsRecompilation;
  private List<String> videoFilePath;
  private List<String> audioFilePathList;

}
