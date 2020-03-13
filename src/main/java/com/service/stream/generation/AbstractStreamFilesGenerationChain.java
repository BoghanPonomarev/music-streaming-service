package com.service.stream.generation;

import com.service.entity.TerminalCommand;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public abstract class AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    protected static final String COMMAND_WORD_PATH = "src/main/resources/ffmpeg-api/bin/ffmpeg";

    protected TerminalCommandExecutor commandExecutor;
    protected StreamFilesGenerationChain nextChainMember;

    @Override
    public String continueAssembleStreamFiles(String firstAssembleSourceFilePath, String secondAssembleSourceFilePath, StreamCompileContext streamCompileContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String startAssembleStreamFiles(StreamCompileContext streamCompileContext) {
        throw new UnsupportedOperationException();
    }

    public abstract TerminalCommand createTerminalCommand();

    protected String executeWithTemporaryResult(String firstParamFile, String secondParamFile, TerminalCommand targetCommand, String outFileExtension) {
        String outputFileName = "src/main/resources/temp/" + UUID.randomUUID() + "." + outFileExtension;
        targetCommand.setOutputFile(outputFileName);
        targetCommand.setFirstInputFile(firstParamFile);
        targetCommand.setSecondInputFile(secondParamFile);

        commandExecutor.execute(targetCommand);
        return outputFileName;
    }

    protected void cleanResources(String... filesToDelete) {
        for (String s : filesToDelete) {
            try {
                FileUtils.forceDelete(new File(s));
            } catch (IOException ex) {
                log.error("Failed during stream compilation in resources cleaning", ex);
            }
        }
    }

}
