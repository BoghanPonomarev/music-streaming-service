package com.service.stream.generation.impl;

import com.service.entity.TerminalCommand;
import com.service.exception.CommandExecutionException;
import com.service.executor.TemporaryCommandExecutor;
import com.service.stream.compile.StreamCompileContext;
import com.service.stream.generation.LastVideoPieceExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mp4parser.IsoFile;
import org.mp4parser.boxes.iso14496.part12.MovieHeaderBox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LastVideoPieceExtractorImpl implements LastVideoPieceExtractor {

    @Value("${compilation.command.word.path}")
    private String commandWordPath;

    private final TemporaryCommandExecutor temporaryCommandExecutor;

    @Override
    public String extractLastVideoPiece(String loopedVideoWithAudioTracks, StreamCompileContext streamCompileContext) {
        double compiledVideoDurationInSec = getVideoDurationInSec(loopedVideoWithAudioTracks);
        double videoDurationInSec = getVideoDurationInSec(streamCompileContext.getVideoFilePath().get(0));
        double startCutPoint = getStartCutPoint(compiledVideoDurationInSec, videoDurationInSec);

        TerminalCommand cutCommand = createCutVideoCommand(startCutPoint, videoDurationInSec - startCutPoint);
        return temporaryCommandExecutor
                .executeWithTemporaryResult(streamCompileContext.getVideoFilePath().get(0), null, cutCommand, "mp4");
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
        return compiledVideoDurationInSec % videoDurationInSec;
    }

    private TerminalCommand createCutVideoCommand(double start, double duration) {
        String videoToStreamCommand = "%s -i %s -ss 00:00:" + start + " -t 00:00:" + duration + " -async 1 %s";
        return new TerminalCommand(videoToStreamCommand, commandWordPath);
    }

}
