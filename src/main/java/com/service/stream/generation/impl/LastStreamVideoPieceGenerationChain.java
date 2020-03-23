package com.service.stream.generation.impl;

import com.service.entity.TerminalCommand;
import com.service.exception.CommandExecutionException;
import com.service.executor.TerminalCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.generation.AbstractStreamFilesGenerationChain;
import com.service.stream.generation.StreamFilesGenerationChain;
import lombok.extern.slf4j.Slf4j;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;

import java.io.IOException;

@Slf4j
public class LastStreamVideoPieceGenerationChain extends AbstractStreamFilesGenerationChain implements StreamFilesGenerationChain {

    public LastStreamVideoPieceGenerationChain(TerminalCommandExecutor commandExecutor, StreamFilesGenerationChain nextChainMember) {
        super(commandExecutor, nextChainMember);
    }

    public String continueAssembleStreamFiles(String loopedVideoWithAudioTracks, String concatenatedAudios, StreamCompileContext streamCompileContext) {
        double compiledVideoDurationInSec = getVideoDurationInSec(loopedVideoWithAudioTracks);
        double videoDurationInSec = getVideoDurationInSec(streamCompileContext.getVideoFilePath().get(0));
        double startCutPoint = getStartCutPoint(compiledVideoDurationInSec, videoDurationInSec);
        TerminalCommand cutCommand = createCutVideoCommand(startCutPoint, videoDurationInSec);
        String cutOriginalVideoFilePath = executeWithTemporaryResult(streamCompileContext.getVideoFilePath().get(0), null, cutCommand, "mp4");

        String concatenatedLoopedVideo = executeWithTemporaryResult(loopedVideoWithAudioTracks, cutOriginalVideoFilePath, createTerminalCommand(), "mp4");

        if (nextChainMember != null) {
            String nextChainMemberResult = nextChainMember.continueAssembleStreamFiles(concatenatedLoopedVideo, concatenatedAudios, streamCompileContext);
            cleanResources(concatenatedLoopedVideo, cutOriginalVideoFilePath);
            return nextChainMemberResult;
        }
        return loopedVideoWithAudioTracks;
    }

    private double getVideoDurationInSec(String filePath) {
        try {
            IsoFile isoFile = new IsoFile(filePath);
            MovieHeaderBox movieHeaderBox = isoFile.getMovieBox().getMovieHeaderBox();
            return movieHeaderBox.getDuration() / (double) movieHeaderBox.getTimescale();
        } catch (IOException ex) {
            log.error("Failure during file duration getting", ex);
            throw new CommandExecutionException("Failure during file duration getting", ex);
        }
    }

    private double getStartCutPoint(double compiledVideoDurationInSec, double videoDurationInSec) {
        double startCutPoint = videoDurationInSec - (compiledVideoDurationInSec % videoDurationInSec) - 1;
        if(startCutPoint < 0) {
            startCutPoint = videoDurationInSec - 0.1;
        }
        return startCutPoint;
    }


    @Override
    public TerminalCommand createTerminalCommand() {
        String videoToStreamCommand = "%s -i \"concat:%s|%s\" -c copy %s";
        return new TerminalCommand(videoToStreamCommand, commandWordPath);
    }

    private TerminalCommand createCutVideoCommand(double start, double end) {
        String videoToStreamCommand = "%s -i %s -ss 00:00:" + start + " -t 00:00:" + end + " -async 1 %s";
        return new TerminalCommand(videoToStreamCommand, commandWordPath);
    }


}
