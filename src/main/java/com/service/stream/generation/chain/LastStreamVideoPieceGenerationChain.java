package com.service.stream.generation.chain;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.generation.LastVideoPieceExtractor;
import com.service.stream.generation.StreamContentFileUpdater;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.UUID;

@Slf4j
public class LastStreamVideoPieceGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    private StreamContentFileUpdater streamContentFileUpdater;
    private LastVideoPieceExtractor lastVideoPieceExtractor;


    public LastStreamVideoPieceGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember,
                                               String commandWordPath, StreamContentFileUpdater streamContentFileUpdater, LastVideoPieceExtractor lastVideoPieceExtractor,
                                               TemporaryCommandExecutor temporaryCommandExecutor) {
        super(commandExecutor, nextChainMember, commandWordPath, temporaryCommandExecutor);
        this.streamContentFileUpdater = streamContentFileUpdater;
        this.lastVideoPieceExtractor = lastVideoPieceExtractor;
    }

    public String continueAssembleStreamFiles(String loopedVideoWithAudioTracks, String concatenatedAudios, StreamCompileContext streamCompileContext) {
        String lastVideoPieceFilePath = executeIfFullCompilation(() -> lastVideoPieceExtractor.extractLastVideoPiece(loopedVideoWithAudioTracks, streamCompileContext),
                streamCompileContext);

        String streamContentVideo = executeIfFullCompilation(() -> concatenateVideos(loopedVideoWithAudioTracks, lastVideoPieceFilePath), streamCompileContext);

        if (nextChainMember != null) {
            String playlistContentFile = streamContentFileUpdater.updateStreamContentFile(streamContentVideo, streamCompileContext.getStreamName(), !streamCompileContext.isOnlyTsRecompilation());
            String nextChainMemberResult = nextChainMember.continueAssembleStreamFiles(playlistContentFile, concatenatedAudios, streamCompileContext);
            cleanResources(lastVideoPieceFilePath);
            return nextChainMemberResult;
        }
        return loopedVideoWithAudioTracks;
    }

    private String concatenateVideos(String firstVideoPath, String secondVideoPath) {
        String textFilePath = createTempTextFile();
        writeConcatenationFilePathToFile(textFilePath, firstVideoPath, secondVideoPath);

        String concatenatedLoopedVideo = temporaryCommandExecutor.executeWithTemporaryResult(textFilePath, null, createTerminalCommand(), "mp4");
        cleanResources(textFilePath);

        return concatenatedLoopedVideo;
    }

    private void writeConcatenationFilePathToFile(String textFilePath, String firstVideoPath, String secondVideoPath) {
        File textFile = new File(textFilePath);

        try(BufferedWriter bufferedFileWriter = new BufferedWriter(new FileWriter(textFile))) {
            bufferedFileWriter.write("file '" + firstVideoPath.substring(firstVideoPath.lastIndexOf("/") + 1) + "'\n");
            bufferedFileWriter.write("file '" + secondVideoPath.substring(secondVideoPath.lastIndexOf("/") + 1) + "'");
        } catch (IOException ex) {
            log.error("Error during writing concatenation files to file", ex);
        }
    }

    private String createTempTextFile() {
        try {
            String newFilePath = "src/main/resources/temp/" + UUID.randomUUID() + ".txt";
            File newTextFile = new File(newFilePath);
            newTextFile.createNewFile();
            return newFilePath;
        } catch (IOException ex) {
            log.error("Error during temp file creation", ex);
        }
        return null;
    }

    @Override
    public TerminalCommand createTerminalCommand() {
        String videoToStreamCommand = "%s -f concat -safe 0 -i %s -c copy %s";
        return new TerminalCommand(videoToStreamCommand, commandWordPath);
    }


}
