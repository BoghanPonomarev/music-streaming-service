package com.service.stream.generation.impl;

import com.service.entity.TerminalCommand;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.generation.AbstractStreamFilesGenerationChain;
import com.service.stream.generation.StreamFilesGenerationChain;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class StreamPlaylistGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamPlaylistGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember, String commandWordPath) {
        super(commandExecutor, nextChainMember, commandWordPath);
    }

    @Override
    public String continueAssembleStreamFiles(String playlistContentFilePath, String concatenatedAudios, StreamCompileContext streamCompileContext) {
        String streamName = streamCompileContext.getStreamName();

        String resultCompilationDirectoryPath = "src/main/resources/stream-source/" + streamName + "/" + streamCompileContext.getIteration();
        String resultStreamPlaylistFilePath = resultCompilationDirectoryPath + "/" + streamName + ".m3u8";
        createCompilationDirectory(resultCompilationDirectoryPath);

        TerminalCommand videoToStreamCommand = fillPlaylistGenerationCommand(createTerminalCommand(), playlistContentFilePath, resultStreamPlaylistFilePath);

        commandExecutor.execute(videoToStreamCommand);
        return resultStreamPlaylistFilePath;
    }

    private TerminalCommand fillPlaylistGenerationCommand(TerminalCommand playlistGenerationCommand, String sourceFile, String resultStreamPlaylistFilePath) {
        playlistGenerationCommand.setOutputFile(resultStreamPlaylistFilePath);
        playlistGenerationCommand.setFirstInputFile(sourceFile);

        return playlistGenerationCommand;
    }

    private void createCompilationDirectory(String resultCompilationDirectory) {
        try {
            Files.createDirectories(Paths.get(resultCompilationDirectory));
        } catch (IOException ex) {
            log.error("Failed during new stream directory", ex);
        }
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        String videoToStreamCommand = "%s -i %s -c:v libx264 -g 125 -r:v 25 -x264opts scenecut=0:keyint_min=125 -f hls -hls_time 30 -hls_list_size 0 -preset veryfast -t 60 -max_muxing_queue_size 1024 %s";
        return new TerminalCommand(videoToStreamCommand, commandWordPath);
    }

}
