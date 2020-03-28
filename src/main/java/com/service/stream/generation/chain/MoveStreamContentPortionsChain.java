package com.service.stream.generation.chain;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import org.springframework.stereotype.Component;

import java.io.File;

public class MoveStreamContentPortionsChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public MoveStreamContentPortionsChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember, String commandWordPath, TemporaryCommandExecutor temporaryCommandExecutor) {
        super(commandExecutor, nextChainMember, commandWordPath, temporaryCommandExecutor);
    }

    @Override
    public String continueAssembleStreamFiles(String nullPath, String nullPAth, StreamCompileContext streamCompileContext) {
     File playlistContentFile = new File();
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        return null;
    }

}
