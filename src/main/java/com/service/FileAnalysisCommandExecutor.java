package com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Optional;

@Slf4j
@Component
public class FileAnalysisCommandExecutor {

  public String executeCommand(DefaultFileCommand command) throws IOException {
    ProcessBuilder processBuilder = new ProcessBuilder(command.getParameters());
    processBuilder.redirectErrorStream(true);

    Optional<Process> commandExecutionProcess = startProcess(processBuilder);
    Process process = commandExecutionProcess.orElseThrow(() -> new ModificationCommandExecutionException("No process")); //TODO
    InputStream inputStream = process.getInputStream();
    waitProcessFinishing(process);
    InputStreamReader isReader = new InputStreamReader(inputStream);
    //Creating a BufferedReader object
    BufferedReader reader = new BufferedReader(isReader);
    StringBuffer sb = new StringBuffer();
    String str;
    while ((str = reader.readLine()) != null) {
      sb.append(str);
    }
    return sb.toString();
  }

  private Optional<Process> startProcess(ProcessBuilder processBuilder) {
    Optional<Process> commandExecutionProcess = Optional.empty();

    try {
      Process start = processBuilder.start();
      commandExecutionProcess = Optional.of(start);
    } catch (Exception ex) {
      commandExecutionProcess.ifPresent(Process::destroy);
      throw new ModificationCommandExecutionException("File modification process failed", ex);
    }
    return commandExecutionProcess;
  }

  private void waitProcessFinishing(Process process) {
    try {
      process.waitFor();
    } catch (InterruptedException ex) {
      log.error("Command execution process failed", ex);
      process.destroy();
      throw new ModificationCommandExecutionException("Waiting for the end of the command was interrupted", ex);
    }
  }//TODO refactor with FileModificationCommandBuilder

}
