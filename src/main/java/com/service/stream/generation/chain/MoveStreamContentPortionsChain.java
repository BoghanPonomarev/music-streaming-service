package com.service.stream.generation.chain;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class MoveStreamContentPortionsChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public MoveStreamContentPortionsChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember, String commandWordPath, TemporaryCommandExecutor temporaryCommandExecutor) {
        super(commandExecutor, nextChainMember, commandWordPath, temporaryCommandExecutor);
    }

    @Override
    public String continueAssembleStreamFiles(String nullPath, String nullPAth, StreamCompileContext streamCompileContext) {
        String streamName = streamCompileContext.getStreamName();
        File playlistContentFile = new File("src/main/resources/stream-source/" + streamName + "/playlist-content");
        String resultCompilationDirectoryPath = "src/main/resources/stream-source/" + streamName + "/" + streamCompileContext.getIteration();

        copyDirectoryContent(playlistContentFile, resultCompilationDirectoryPath);

        return resultCompilationDirectoryPath + "/" + streamName + ".m3u8";
    }

    private void copyDirectoryContent(File playlistContentFile, String resultCompilationDirectoryPath) {
        try {
            File iterationContentDirectory = new File(resultCompilationDirectoryPath);
            FileUtils.copyDirectory(playlistContentFile, iterationContentDirectory);
        } catch (IOException ex) {
            log.error("Can not move content to iteration file", ex);
        }
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        return null;
    }

}
