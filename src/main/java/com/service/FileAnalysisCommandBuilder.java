package com.service;

import com.amazonaws.util.StringUtils;
import com.service.entity.FileAnalysisSpecification;
import org.springframework.stereotype.Component;

@Component
public class FileAnalysisCommandBuilder {

  private static final String ffprobeCommandPath = "src/main/resources/ffmpeg-api/bin/ffprobe";

  public DefaultFileCommand buildAnalysisQuery(FileAnalysisSpecification fileAnalysisSpecification) {
    DefaultFileCommand defaultFileCommand = new DefaultFileCommand();
    defaultFileCommand.addParameter(ffprobeCommandPath);

    appendIfNotNUll(defaultFileCommand, "-i", fileAnalysisSpecification.getFilePath());
    if (fileAnalysisSpecification.getIsGetMetadata()) {
      defaultFileCommand.addParameter("-show_format");
    }

    return defaultFileCommand;
  }

  private <T> void appendIfNotNUll(DefaultFileCommand defaultFileCommand, String appendix, T value) {
    String textValue = String.valueOf(value);
    if (!StringUtils.isNullOrEmpty(textValue) && !textValue.equals("null")) {
      defaultFileCommand.addParameter(appendix);
      defaultFileCommand.addParameter(textValue);
    }//TODO code duplication , see FileModificationCommandBuilder
  }
}
