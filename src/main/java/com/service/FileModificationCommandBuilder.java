package com.service;

import com.amazonaws.util.StringUtils;
import com.service.entity.FileModificationSpecification;
import org.springframework.stereotype.Component;

@Component
public class FileModificationCommandBuilder {

  private static final String ffmpegCommandPath = "src/main/resources/ffmpeg-api/bin/ffmpeg";

  public DefaultFileCommand buildModificationQuery(FileModificationSpecification fileModificationSpecification) {
    DefaultFileCommand defaultFileCommand = new DefaultFileCommand();
    defaultFileCommand.addParameter(ffmpegCommandPath);

    appendIfNotNull(defaultFileCommand, "-ss", fileModificationSpecification.getStartTime());
    appendIfTrue(defaultFileCommand, "-stream_loop", fileModificationSpecification.getIsPLayInLoop());
    appendIfTrue(defaultFileCommand, "-1", fileModificationSpecification.getIsPLayInLoop());

    appendIfNotNull(defaultFileCommand, "-i", fileModificationSpecification.getFirstFilePath());

    appendIfTrue(defaultFileCommand, "-bsf:v", fileModificationSpecification.getIsStream());
    appendIfTrue(defaultFileCommand, "h264_mp4toannexb", fileModificationSpecification.getIsStream());

    appendIfNotNull(defaultFileCommand, "-i", fileModificationSpecification.getSecondFilePath());

    addLoopRelatedParameters(fileModificationSpecification, defaultFileCommand);

    appendIfNotNull(defaultFileCommand, "-t", fileModificationSpecification.getDurationTime());

    appendIfTrue(defaultFileCommand, "-an", fileModificationSpecification.getIsRemoveAudio());

    appendIfNotNull(defaultFileCommand, "-acodec", fileModificationSpecification.getAudioCodec());
    appendIfNotNull(defaultFileCommand, "-vcodec", fileModificationSpecification.getAudioCodec());
    appendIfNotNull(defaultFileCommand, "-c", fileModificationSpecification.getGeneralCodec());

    appendIfTrue(defaultFileCommand, "-hls_list_size", fileModificationSpecification.getIsStream());
    appendIfTrue(defaultFileCommand, "0", fileModificationSpecification.getIsStream());
    appendIfNotNull(defaultFileCommand, fileModificationSpecification.getResultFilePath());

    return defaultFileCommand;
  }

  private void addLoopRelatedParameters(FileModificationSpecification fileModificationSpecification, DefaultFileCommand defaultFileCommand) {
    appendIfTrue(defaultFileCommand, "-shortest", fileModificationSpecification.getIsPLayInLoop());
    appendIfTrue(defaultFileCommand, "-map", fileModificationSpecification.getIsPLayInLoop());
    appendIfTrue(defaultFileCommand, "0:v:0", fileModificationSpecification.getIsPLayInLoop());
    appendIfTrue(defaultFileCommand, "-map", fileModificationSpecification.getIsPLayInLoop());
    appendIfTrue(defaultFileCommand, "1:a:0", fileModificationSpecification.getIsPLayInLoop());
    appendIfTrue(defaultFileCommand, "-y", fileModificationSpecification.getIsPLayInLoop());
  }


  private <T> void appendIfNotNull(DefaultFileCommand defaultFileCommand, T value) {
    String textValue = String.valueOf(value);
    if (!StringUtils.isNullOrEmpty(textValue) && !textValue.equals("null")) {
      defaultFileCommand.addParameter(textValue);
    }
  }

  private <T> void appendIfNotNull(DefaultFileCommand defaultFileCommand, String appendix, T value) {
    String textValue = String.valueOf(value);
    if (!StringUtils.isNullOrEmpty(textValue) && !textValue.equals("null")) {
      defaultFileCommand.addParameter(appendix);
      defaultFileCommand.addParameter(textValue);
    }
  }

  private void appendIfTrue(DefaultFileCommand defaultFileCommand, String appendix, Boolean condition) {
    if(Boolean.TRUE.equals(condition)) {
      defaultFileCommand.addParameter(appendix);
    }
  }


}
