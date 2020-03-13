package com.service.stream.generation.impl;

import com.service.entity.TerminalCommand;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.generation.AbstractStreamFilesGenerationChain;
import com.service.stream.generation.StreamFilesGenerationChain;

public class StreamSilentVideoGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamSilentVideoGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember) {
        super(commandExecutor, nextChainMember);
    }

    @Override
    public String continueAssembleStreamFiles(String videoFilePath, String concatenatedAudios, StreamCompileContext streamCompileContext) {
        String silentVideoFilePath = executeWithTemporaryResult(videoFilePath, concatenatedAudios, createTerminalCommand(), "mp4");

        if(nextChainMember != null) {
            String nextChainMemberResult = nextChainMember.continueAssembleStreamFiles(silentVideoFilePath, concatenatedAudios, streamCompileContext);
            cleanResources(silentVideoFilePath);
            return nextChainMemberResult;
        }
        return silentVideoFilePath;
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        String removeAudioFromFileCommand = "%s -i %s -i %s -an -acodec copy -vcodec copy %s";
        return new TerminalCommand(removeAudioFromFileCommand, COMMAND_WORD_PATH);
    }

}
