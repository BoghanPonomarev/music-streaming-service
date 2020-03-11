package com.service.config;

import com.service.entity.TerminalCommand;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;

@Configuration
public class CommandConfig {

  private String COMMAND_WORD_PATH = "src/main/resources/ffmpeg-api/bin/ffmpeg";

  @Bean
  @Order(1)
  @Scope("prototype")
  @Qualifier("removeAudioFromFileCommand")
  public TerminalCommand removeAudioFromFileCommand() {
    String removeAudioFromFileCommand = "%s -i %s -i %s -an -acodec copy -vcodec copy %s";
    return new TerminalCommand(removeAudioFromFileCommand, COMMAND_WORD_PATH);
  }

  @Bean
  @Order(2)
  @Scope("prototype")
  @Qualifier("concatenateAudiosCommand")
  public TerminalCommand concatenateAudiosCommand() {
    String concatenateAudiosCommand = "%s -i \"concat:%s|%s\" -acodec copy %s";
    return new TerminalCommand(concatenateAudiosCommand, COMMAND_WORD_PATH);
  }

  @Bean
  @Order(3)
  @Scope("prototype")
  @Qualifier("mergeLoopedVideoBeforeAudioFinishCommand")
  public TerminalCommand mergeLoopedVideoBeforeAudioFinishCommand() {
    String mergeLoopedVideoBeforeAudioFinishCommand = "%s -stream_loop -1 -i %s -i %s -shortest -map 0:v:0 -map 1:a:0 -y %s";
    return new TerminalCommand(mergeLoopedVideoBeforeAudioFinishCommand, COMMAND_WORD_PATH);
  }

  @Bean
  @Order(4)
  @Scope("prototype")
  @Qualifier("videoToStreamCommand")
  public TerminalCommand videoToStreamCommand() {
    String videoToStreamCommand = "%s -i %s -bsf:v h264_mp4toannexb -c copy -hls_list_size 0 %s";
    return new TerminalCommand(videoToStreamCommand, COMMAND_WORD_PATH);
  }

}
