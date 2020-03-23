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
    LastStreamVideoPieceGenerationChain lastStreamVideoPieceGenerationChain = new LastStreamVideoPieceGenerationChain(terminalCommandExecutor, streamPlaylistGenerationChain);
    StreamLoopedVideoGenerationChain streamLoopedVideoGenerationChain = new StreamLoopedVideoGenerationChain(terminalCommandExecutor, lastStreamVideoPieceGenerationChain);
    StreamConcatenatedAudiosGenerationChain streamConcatenatedAudiosGenerationChain = new StreamConcatenatedAudiosGenerationChain(terminalCommandExecutor, streamLoopedVideoGenerationChain);

    return new StreamPreviewImageGenerationChain(terminalCommandExecutor, streamConcatenatedAudiosGenerationChain);
  }

}
