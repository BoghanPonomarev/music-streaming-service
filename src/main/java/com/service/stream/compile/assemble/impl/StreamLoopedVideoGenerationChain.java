package com.service.stream.compile.assemble.impl;

import com.service.entity.TerminalCommand;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.compile.assemble.AbstractStreamFilesGenerationChain;
import com.service.stream.compile.assemble.StreamFilesGenerationChain;

import java.util.List;

public class StreamLoopedVideoGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamLoopedVideoGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember) {
        super(commandExecutor, nextChainMember);
    }

    @Override
    public String continueAssembleStreamFiles(String silentVideoFilePath, String concatenatedAudios, StreamCompileContext streamCompileContext) {
        String loopedVideoWithAudioTracks = executeWithTemporaryResult(silentVideoFilePath, concatenatedAudios, createTerminalCommand(), "mp4");

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
        return new TerminalCommand(mergeLoopedVideoBeforeAudioFinishCommand, COMMAND_WORD_PATH);
    }

}
