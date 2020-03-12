package com.service.stream.compile;

import com.service.entity.TerminalCommand;
import com.service.executor.TerminalCommandExecutorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class StreamCompileChainImpl implements StreamCompileChain {

    private final TerminalCommand extractImageCommand;//TODo remove to local
    private final TerminalCommand videoToStreamCommand;
    private final TerminalCommand concatenateAudiosCommand;
    private final TerminalCommand removeAudioFromFileCommand;
    private final TerminalCommand mergeLoopedVideoBeforeAudioFinishCommand;

    private final TerminalCommandExecutorImpl commandExecutor;

    @Override
    public String compileStream(StreamCompileContext streamCompileContext) {
        generateImage(streamCompileContext.getStreamName(), streamCompileContext.getVideoFilePath());
        String concatenatedAudios = concatenateAudios(streamCompileContext.getAudioFilePathList());
        String silentVideoFilePath = executeWithParams(streamCompileContext.getVideoFilePath(), concatenatedAudios, removeAudioFromFileCommand, "mp4");
        String loopedVideoWithAudio = executeWithParams(silentVideoFilePath, concatenatedAudios, mergeLoopedVideoBeforeAudioFinishCommand, "mp4");

        String resultFilePath = compileStream(streamCompileContext.getStreamName(), loopedVideoWithAudio, streamCompileContext.getIteration());
        cleanResources(silentVideoFilePath, loopedVideoWithAudio, concatenatedAudios);
        return resultFilePath;
    }

    private String concatenateAudios(List<String> audioFilesPaths) {
        String mainFilePath = audioFilesPaths.get(0);
        for (int i = 1; i < audioFilesPaths.size(); i++) {
            String tmpMainFilePath = executeWithParams(mainFilePath, audioFilesPaths.get(i), concatenateAudiosCommand, "mp3");
            if (i > 1) {
                cleanResources(mainFilePath);
            }
            mainFilePath = tmpMainFilePath;
        }

        return mainFilePath;
    }

    private String executeWithParams(String firstParamFile, String secondParamFile, TerminalCommand targetCommand, String outFileExtension) {
        String outputFileName = "src/main/resources/temp/" + UUID.randomUUID() + "." + outFileExtension;
        targetCommand.setOutputFile(outputFileName);
        targetCommand.setFirstInputFile(firstParamFile);
        targetCommand.setSecondInputFile(secondParamFile);

        commandExecutor.execute(targetCommand);
        return outputFileName;
    }

    private String compileStream(String streamName, String sourceFile, Integer compilationIteration) {
        String resultCompilationDirectory = "src/main/resources/stream-source/" + streamName + "/" + compilationIteration;
        createCompilationDirectory(resultCompilationDirectory);

        String mainStreamFilePath = resultCompilationDirectory + "/" + streamName + ".m3u8";
        videoToStreamCommand.setOutputFile(mainStreamFilePath);
        videoToStreamCommand.setFirstInputFile(sourceFile);

        commandExecutor.execute(videoToStreamCommand);
        return mainStreamFilePath;
    }

    private String generateImage(String streamName, String videoPath) {
        String resultGenerationFile = "src/main/resources/stream-source/" + streamName + "/" + streamName + "-pr.jpg";
        extractImageCommand.setOutputFile(resultGenerationFile);
        extractImageCommand.setFirstInputFile(videoPath);

        commandExecutor.execute(extractImageCommand);
        return resultGenerationFile;
    }

    private void createCompilationDirectory(String resultCompilationDirectory) {
        try {
            Files.createDirectories(Paths.get(resultCompilationDirectory));
        } catch (IOException ex) {
            log.error("Failed during new stream directory", ex);
        }
    }

    private void cleanResources(String... filesToDelete) {
        for (String s : filesToDelete) {
            try {
                FileUtils.forceDelete(new File(s));
            } catch (IOException ex) {
                log.error("Failed during stream compilation in resources cleaning", ex);
            }
        }
    }

}

