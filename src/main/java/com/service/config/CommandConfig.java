package com.service.config;

import com.service.DefaultFileCommand;
import com.service.entity.FileModificationCommand;
import com.service.entity.FileModificationSpecification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class CommandConfig {

  private String COMMAND_WORD_PATH = "src/main/resources/ffmpeg-api/bin/ffmpeg";

  @Bean
  @Qualifier("removeAudioFromFileSpecification")
  public FileModificationSpecification removeAudioFromFileSpecification() {
    return FileModificationSpecification.builder().isRemoveAudio(true)
            .audioCodec("copy").videoCodec("copy").build();
  }

  @Bean
  @Qualifier("mergeLoopedVideoBeforeAudioFinishSpecification")
  public FileModificationSpecification mergeLoopedVideoBeforeAudioFinishSpecification() {
    return FileModificationSpecification.builder().isPLayInLoop(true).build();
  }

  @Bean
  @Qualifier("videoToStream")
  public FileModificationSpecification videoToStream() {
    return FileModificationSpecification.builder().isStream(true).generalCodec("copy").build();
  }

  @Bean
  @Order(1)
  @Qualifier("removeAudioFromFileCommand")
  public FileModificationCommand removeAudioFromFileCommand() {
    String removeAudioFromFileCommand = "%s -i %s -i %s -an -acodec copy -vcodec copy %s";
    return new FileModificationCommand(removeAudioFromFileCommand, COMMAND_WORD_PATH);
  }

  @Bean
  @Order(2)
  @Qualifier("mergeLoopedVideoBeforeAudioFinishCommand")
  public FileModificationCommand mergeLoopedVideoBeforeAudioFinishCommand() {
    String mergeLoopedVideoBeforeAudioFinishCommand = "%s -stream_loop -1 -i %s -i %s -shortest -map 0:v:0 -map 1:a:0 -y %s";
    return new FileModificationCommand(mergeLoopedVideoBeforeAudioFinishCommand, COMMAND_WORD_PATH);
  }

  @Bean
  @Order(3)
  @Qualifier("videoToStreamCommand")
  public FileModificationCommand videoToStreamCommand() {
    String videoToStreamCommand = "%s -i %s -bsf:v h264_mp4toannexb -c copy -hls_list_size 0 %s";
    return new FileModificationCommand(videoToStreamCommand, COMMAND_WORD_PATH);
  }

}
