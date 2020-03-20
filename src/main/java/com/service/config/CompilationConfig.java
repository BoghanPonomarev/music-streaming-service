package com.service.config;

import com.service.executor.TerminalCommandExecutor;
import com.service.stream.generation.StreamFilesGenerationChain;
import com.service.stream.generation.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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