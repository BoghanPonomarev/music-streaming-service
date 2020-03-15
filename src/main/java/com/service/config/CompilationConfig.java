package com.service.config;

import com.service.config.listener.ApplicationContextStartListener;
import com.service.entity.StreamPortion;
import com.service.entity.model.Stream;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.generation.StreamFilesGenerationChain;
import com.service.stream.generation.impl.*;
import com.service.stream.starter.StreamContentInjector;
import com.service.system.SystemResourceCleaner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

@Configuration
public class CompilationConfig {

  @Bean
  public StreamFilesGenerationChain streamFilesGenerationChain(TerminalCommandExecutor terminalCommandExecutor) {
    StreamPlaylistGenerationChain streamPlaylistGenerationChain = new StreamPlaylistGenerationChain(terminalCommandExecutor, null);
    StreamLoopedVideoGenerationChain streamLoopedVideoGenerationChain = new StreamLoopedVideoGenerationChain(terminalCommandExecutor, streamPlaylistGenerationChain);
    StreamSilentVideoGenerationChain silentVideoGenerationChain = new StreamSilentVideoGenerationChain(terminalCommandExecutor, streamLoopedVideoGenerationChain);
    StreamConcatenatedAudiosGenerationChain streamConcatenatedAudiosGenerationChain = new StreamConcatenatedAudiosGenerationChain(terminalCommandExecutor, silentVideoGenerationChain);

    return new StreamPreviewImageGenerationChain(terminalCommandExecutor, streamConcatenatedAudiosGenerationChain);
  }

}
