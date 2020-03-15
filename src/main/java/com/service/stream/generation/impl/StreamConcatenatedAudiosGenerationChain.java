package com.service.stream.generation.impl;

import com.service.entity.TerminalCommand;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.generation.AbstractStreamFilesGenerationChain;
import com.service.stream.generation.StreamFilesGenerationChain;

import java.util.List;

public class StreamConcatenatedAudiosGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamConcatenatedAudiosGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember) {
        super(commandExecutor, nextChainMember);
    }

    @Override
    public String continueAssembleStreamFiles(String firstAssembleSourceFilePath, String secondAssembleSourceFilePath, StreamCompileContext streamCompileContext) {
        List<String> audioFilePathList = streamCompileContext.getAudioFilePathList();
        String longestConcatenatedFile = concatenateAllAudios(audioFilePathList);

        if(nextChainMember != null) {
            String firstVideoPath = streamCompileContext.getVideoFilePath().get(0);
            String nextChainMemberResult = nextChainMember.continueAssembleStreamFiles(firstVideoPath, longestConcatenatedFile, streamCompileContext);
            cleanResources(longestConcatenatedFile);
            return nextChainMemberResult;
        }
        return longestConcatenatedFile;
    }

    private String concatenateAllAudios( List<String> audioFilePathList) {
        String longestConcatenatedFile = audioFilePathList.get(0);

        for (int i = 1; i < audioFilePathList.size(); i++) {
            String tmpMainFilePath = executeWithTemporaryResult(longestConcatenatedFile, audioFilePathList.get(i), createTerminalCommand(), "mp3");
            if (i > 1) {
                cleanResources(longestConcatenatedFile);
            }
            longestConcatenatedFile = tmpMainFilePath;
        }
        return longestConcatenatedFile;
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        String concatenateAudiosCommand = "%s -i \"concat:%s|%s\" -acodec copy %s";
        return new TerminalCommand(concatenateAudiosCommand, COMMAND_WORD_PATH);
    }

}
