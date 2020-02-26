package com.service.service.strategy;

import com.service.entity.FileModificationCommand;
import com.service.executor.FileModificationCommandExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class StreamCompileStrategyImpl implements StreamCompileStrategy {


  private final FileModificationCommand videoToStreamCommand;
  private final FileModificationCommand removeAudioFromFileCommand;
  private final FileModificationCommand mergeLoopedVideoBeforeAudioFinishCommand;

  private final FileModificationCommandExecutor commandExecutor;

  @Override
  public String compileStream(StreamCompileContext streamCompileContext) {
    String firstAudioElement = streamCompileContext.getAudioFilePathList().get(0);
    String silentVideoFilePath = executeWithParams(streamCompileContext.getVideoFilePath(), firstAudioElement, removeAudioFromFileCommand, "mp4");
    String loopedVideoWithAudio = executeWithParams(silentVideoFilePath, firstAudioElement, mergeLoopedVideoBeforeAudioFinishCommand, "mp4");

    String resultFilePath = compileStream(streamCompileContext.getStreamName(), loopedVideoWithAudio, streamCompileContext.getIteration());
    cleanResources(silentVideoFilePath, loopedVideoWithAudio);
    return resultFilePath;
  }

  private String executeWithParams(String firstParamFile, String secondParamFile, FileModificationCommand targetCommand, String outFileExtension) {
    String outputFileName = "src/main/resources/temp/" + UUID.randomUUID() + "." + outFileExtension;
    targetCommand.setOutputFile(outputFileName);
    targetCommand.setFirstInputFile(firstParamFile);
    targetCommand.setSecondInputFile(secondParamFile);

    commandExecutor.executeCommand(targetCommand);
    return outputFileName;
  }

  private String compileStream(String streamName, String sourceFile, Integer compilationIteration) {
    String resultCompilationDirectory = "src/main/resources/stream-source/" + streamName + "/" + compilationIteration;
    createCompilationDirectory(resultCompilationDirectory);

    String mainStreamFilePath = resultCompilationDirectory + "/" + streamName + ".m3u8";
    videoToStreamCommand.setOutputFile(mainStreamFilePath);
    videoToStreamCommand.setFirstInputFile(sourceFile);

    commandExecutor.executeCommand(videoToStreamCommand);
    return mainStreamFilePath;
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

