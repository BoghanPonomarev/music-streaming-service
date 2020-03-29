package com.service.config;

import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.generation.LastVideoPieceExtractor;
import com.service.stream.generation.StreamContentFileUpdater;
import com.service.stream.generation.chain.*;
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
    public StreamFilesGenerationChain streamStartGenerationChainMember(TerminalCommandExecutor terminalCommandExecutor,
                                                                       MoveStreamContentPortionsChain moveStreamContentPortionsChain,
                                                                       StreamContentFileUpdater streamContentFileUpdater,
                                                                       LastVideoPieceExtractor lastVideoPieceExtractor,
                                                                       TemporaryCommandExecutor temporaryCommandExecutor) {
        StreamPlaylistGenerationChain streamPlaylistGenerationChain = new StreamPlaylistGenerationChain(terminalCommandExecutor, moveStreamContentPortionsChain, commandWordPath, temporaryCommandExecutor);
        LastStreamVideoPieceGenerationChain lastStreamVideoPieceGenerationChain = new LastStreamVideoPieceGenerationChain(terminalCommandExecutor, streamPlaylistGenerationChain, commandWordPath, streamContentFileUpdater, lastVideoPieceExtractor, temporaryCommandExecutor);
        StreamLoopedVideoGenerationChain streamLoopedVideoGenerationChain = new StreamLoopedVideoGenerationChain(terminalCommandExecutor, lastStreamVideoPieceGenerationChain, commandWordPath, temporaryCommandExecutor);
        StreamSilentVideoGenerationChain silentVideoGenerationChain = new StreamSilentVideoGenerationChain(terminalCommandExecutor, streamLoopedVideoGenerationChain, commandWordPath, temporaryCommandExecutor);
        StreamConcatenatedAudiosGenerationChain streamConcatenatedAudiosGenerationChain = new StreamConcatenatedAudiosGenerationChain(terminalCommandExecutor, silentVideoGenerationChain, commandWordPath, temporaryCommandExecutor);

        return new StreamPreviewImageGenerationChain(terminalCommandExecutor, streamConcatenatedAudiosGenerationChain, commandWordPath, temporaryCommandExecutor);
    }

    @Bean
    @Qualifier("moveStreamContentPortionsChainMember")
    public MoveStreamContentPortionsChain moveStreamContentPortionsChainMember(TerminalCommandExecutor terminalCommandExecutor, TemporaryCommandExecutor temporaryCommandExecutor) {
        return new MoveStreamContentPortionsChain(terminalCommandExecutor, null, commandWordPath, temporaryCommandExecutor);
    }

}
