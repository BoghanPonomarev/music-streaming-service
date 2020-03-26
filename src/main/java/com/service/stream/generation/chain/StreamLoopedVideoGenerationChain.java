package com.service.stream.generation.chain;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;

public class StreamLoopedVideoGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamLoopedVideoGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember,
                                            String commandWordPath, TemporaryCommandExecutor temporaryCommandExecutor) {
        super(commandExecutor, nextChainMember, commandWordPath, temporaryCommandExecutor);
    }

    @Override
    public String continueAssembleStreamFiles(String silentVideoFilePath, String concatenatedAudios, StreamCompileContext streamCompileContext) {
        String loopedVideoWithAudioTracks = temporaryCommandExecutor.executeWithTemporaryResult(silentVideoFilePath, concatenatedAudios, createTerminalCommand(), "mp4");

        if(nextChainMember != null) {
            String nextChainMemberResult = nextChainMember.continueAssembleStreamFiles(loopedVideoWithAudioTracks, concatenatedAudios, streamCompileContext);
            cleanResources(loopedVideoWithAudioTracks);
            return nextChainMemberResult;
        }
        return loopedVideoWithAudioTracks;
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        String mergeLoopedVideoBeforeAudioFinishCommand = "%s -stream_loop -1 -i %s -i %s -shortest -map 0:v:0 -map 1:a:0 -y %s";
        return new TerminalCommand(mergeLoopedVideoBeforeAudioFinishCommand, commandWordPath);
    }

}
