package com.service.config;

import com.service.executor.TerminalCommandExecutor;
import com.service.stream.generation.StreamFilesGenerationChain;
import com.service.stream.generation.impl.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompilationConfig {

  @Value("${compilation.command.word.path}")
  private String commandWordPath;

  @Bean
  @Qualifier("streamStartGenerationChainMember")
  public StreamFilesGenerationChain streamStartGenerationChainMember(TerminalCommandExecutor terminalCommandExecutor, StreamPlaylistGenerationChain streamPlaylistGenerationChain) {
    LastStreamVideoPieceGenerationChain lastStreamVideoPieceGenerationChain = new LastStreamVideoPieceGenerationChain(terminalCommandExecutor, streamPlaylistGenerationChain, commandWordPath);
    StreamLoopedVideoGenerationChain streamLoopedVideoGenerationChain = new StreamLoopedVideoGenerationChain(terminalCommandExecutor, lastStreamVideoPieceGenerationChain, commandWordPath);
    StreamSilentVideoGenerationChain silentVideoGenerationChain = new StreamSilentVideoGenerationChain(terminalCommandExecutor, streamLoopedVideoGenerationChain, commandWordPath);
    StreamConcatenatedAudiosGenerationChain streamConcatenatedAudiosGenerationChain = new StreamConcatenatedAudiosGenerationChain(terminalCommandExecutor, silentVideoGenerationChain, commandWordPath);

    return new StreamPreviewImageGenerationChain(terminalCommandExecutor, streamConcatenatedAudiosGenerationChain, commandWordPath);
  }

  @Bean
  @Qualifier("streamPlaylistGenerationChainMember")
  public StreamPlaylistGenerationChain streamPlaylistGenerationChainMember(TerminalCommandExecutor terminalCommandExecutor) {
    return new StreamPlaylistGenerationChain(terminalCommandExecutor, null, commandWordPath);
  }

}
