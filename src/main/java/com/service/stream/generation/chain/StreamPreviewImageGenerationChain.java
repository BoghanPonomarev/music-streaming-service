package com.service.stream.generation.chain;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;

public class StreamPreviewImageGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public StreamPreviewImageGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember,
                                             String commandWordPath, TemporaryCommandExecutor temporaryCommandExecutor) {
        super(commandExecutor, nextChainMember, commandWordPath, temporaryCommandExecutor);
    }

    @Override
    public String startAssembleStreamFiles(StreamCompileContext streamCompileContext) {
        TerminalCommand extractImageCommand = fillPreviewImageGenerationCommand(createTerminalCommand(), streamCompileContext);

        commandExecutor.execute(extractImageCommand);
        if(nextChainMember != null) {
            return nextChainMember.continueAssembleStreamFiles(null, null, streamCompileContext);
        }
        return extractImageCommand.getOutputFile();
    }

    private TerminalCommand fillPreviewImageGenerationCommand(TerminalCommand extractImageCommand, StreamCompileContext streamCompileContext) {
        String streamName = streamCompileContext.getStreamName();

        String resultFilePath = "src/main/resources/stream-source/" + streamName + "/" + streamName + "-pr.jpg";
        extractImageCommand.setOutputFile(resultFilePath);
        extractImageCommand.setFirstInputFile(streamCompileContext.getVideoFilePath().get(0));
        return extractImageCommand;
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        String extractImageCommand= "%s -y -i %s -qscale:v 4 -frames:v 1 %s";
        return new TerminalCommand(extractImageCommand, commandWordPath);
    }

}
