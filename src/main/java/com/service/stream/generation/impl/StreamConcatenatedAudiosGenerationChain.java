package com.service.stream.generation.impl;

import com.service.entity.TerminalCommand;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.generation.AbstractStreamFilesGenerationChain;
import com.service.stream.generation.StreamFilesGenerationChain;

import java.util.Collections;
import java.util.List;

public class StreamConcatenatedAudiosGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamConcatenatedAudiosGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember) {
        super(commandExecutor, nextChainMember);
    }

    @Override
    public String continueAssembleStreamFiles(String firstAssembleSourceFilePath, String secondAssembleSourceFilePath, StreamCompileContext streamCompileContext) {
        List<String> audioFilePathList = streamCompileContext.getAudioFilePathList();
        Collections.shuffle(audioFilePathList);

        String longestConcatenatedFile = executeWithTemporaryResult(null, null, createConcatenateCommand(audioFilePathList), "mp3");;

        if (nextChainMember != null) {
            String firstVideoPath = streamCompileContext.getVideoFilePath().get(0);
            String nextChainMemberResult = nextChainMember.continueAssembleStreamFiles(firstVideoPath, longestConcatenatedFile, streamCompileContext);
            cleanResources(longestConcatenatedFile);
            return nextChainMemberResult;
        }
        return longestConcatenatedFile;
    }

    @Override
    public TerminalCommand createTerminalCommand() {
       return null;
    }

    private TerminalCommand createConcatenateCommand(List<String> filePathsToConcatenate) {
        StringBuilder concatenateCommandBuilder = new StringBuilder("%s -i \"concat:");

        concatenateCommandBuilder.append(filePathsToConcatenate.get(0));
        for (int i = 1; i < filePathsToConcatenate.size(); i++) {
            concatenateCommandBuilder.append("|").append(filePathsToConcatenate.get(i));
        }

        concatenateCommandBuilder.append("\" -acodec copy %s");
        return new TerminalCommand(concatenateCommandBuilder.toString(), commandWordPath);
    }

}
