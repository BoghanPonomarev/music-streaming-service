package com.service.stream.generation.chain;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public abstract class AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    protected String commandWordPath;

    protected TerminalCommandExecutor commandExecutor;
    protected TemporaryCommandExecutor temporaryCommandExecutor;
    protected StreamFilesGenerationChain nextChainMember;

    public AbstractStreamFilesGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember,
                                              String commandWordPath, TemporaryCommandExecutor temporaryCommandExecutor) {
        this.commandExecutor = commandExecutor;
        this.nextChainMember = nextChainMember;
        this.commandWordPath = commandWordPath;
        this.temporaryCommandExecutor = temporaryCommandExecutor;
    }

    @Override
    public String continueAssembleStreamFiles(String firstAssembleSourceFilePath, String secondAssembleSourceFilePath, StreamCompileContext streamCompileContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String startAssembleStreamFiles(StreamCompileContext streamCompileContext) {
        throw new UnsupportedOperationException();
    }

    public abstract TerminalCommand createTerminalCommand();

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
