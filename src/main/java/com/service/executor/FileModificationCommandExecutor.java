package com.service.executor;

import com.service.entity.FileModificationCommand;
import com.service.exception.ModificationCommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FileModificationCommandExecutor {

  public void executeCommand(FileModificationCommand command) {
    ProcessBuilder processBuilder = new ProcessBuilder(command.toExecutableParams());
    processBuilder.redirectErrorStream(true);

    Optional<Process> commandExecutionProcess = startProcess(processBuilder);
    commandExecutionProcess.ifPresent(this::waitProcessFinishing);
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
      do {
        process.waitFor(5, TimeUnit.SECONDS);
        log.info("Process alive - {} ", process.isAlive());
        process.getInputStream().read();
      } while (process.isAlive());
    } catch (InterruptedException | IOException ex) {
      log.error("Command execution process failed", ex);
      process.destroy();
      throw new ModificationCommandExecutionException("Waiting for the end of the command was interrupted", ex);
    }
  }


}
