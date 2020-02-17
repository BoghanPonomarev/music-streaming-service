package com.service.config;

import com.service.entity.FileModificationSpecification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandConfig {

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

}
