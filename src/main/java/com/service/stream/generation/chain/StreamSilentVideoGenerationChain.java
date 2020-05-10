package com.service.stream.generation.chain;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;

public class StreamSilentVideoGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamSilentVideoGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember,
                                            String commandWordPath, TemporaryCommandExecutor temporaryCommandExecutor) {
        super(commandExecutor, nextChainMember, commandWordPath, temporaryCommandExecutor);
    }

    @Override
    public String continueAssembleStreamFiles(String videoFilePath, String concatenatedAudios, StreamCompileContext streamCompileContext) {
        String silentVideoFilePath = executeIfFullCompilation(() ->
                temporaryCommandExecutor.executeWithTemporaryResult(videoFilePath, concatenatedAudios, createTerminalCommand(), "mp4"),
                streamCompileContext);

        if (nextChainMember != null) {
            String nextChainMemberResult = nextChainMember.continueAssembleStreamFiles(silentVideoFilePath, concatenatedAudios, streamCompileContext);
            cleanResources(silentVideoFilePath);
            return nextChainMemberResult;
        }
        return silentVideoFilePath;
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        String removeAudioFromFileCommand = "%s -i %s -i %s -an -acodec copy -vcodec copy %s";
        return new TerminalCommand(removeAudioFromFileCommand, commandWordPath);
    }

}
