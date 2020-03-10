package com.service.executor;

import com.service.entity.FileModificationCommand;
import com.service.exception.ModificationCommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FileModificationCommandExecutor {

  public void executeCommand(FileModificationCommand command) {
    CommandLine cmdLine = CommandLine.parse(String.join(" ", command.toExecutableParams()));
    DefaultExecutor executor = new DefaultExecutor();
    try {
      int exitValue = executor.execute(cmdLine);
      System.out.println("Result: " + exitValue);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
      BufferedReader bur= new BufferedReader(new InputStreamReader(process.getInputStream()));
      do {
        process.waitFor(5, TimeUnit.SECONDS);
        log.info("Process alive - {} ", process.isAlive());
      } while (process.isAlive());
    } catch (InterruptedException ex) {
      log.error("Command execution process failed", ex);
      process.destroy();
      throw new ModificationCommandExecutionException("Waiting for the end of the command was interrupted", ex);
    }
  }


}
