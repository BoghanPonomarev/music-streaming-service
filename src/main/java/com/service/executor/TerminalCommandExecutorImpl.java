package com.service.executor;

import com.service.entity.TerminalCommand;
import com.service.exception.CommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.stereotype.Component;


import java.io.IOException;


@Slf4j
@Component
public class TerminalCommandExecutorImpl implements TerminalCommandExecutor {

    public void execute(TerminalCommand terminalCommand) {
        String textTerminalCommand = terminalCommand.toCommandTextLine();
        log.info("Terminal command is about executing, command - {}", terminalCommand);

        CommandLine commandLine = CommandLine.parse(textTerminalCommand);
        try {
            executeCommandLine(commandLine);
        } catch (IOException ex) {
            log.error("Command execution failed, command - {}", commandLine, ex);
            throw new CommandExecutionException("Command execution failed", ex);
        }
    }

    private void executeCommandLine(CommandLine commandLine) throws IOException {
        DefaultExecutor executor = new DefaultExecutor();
        int resultCode = executor.execute(commandLine);

        log.info("Command {} finished with code - {}", commandLine, resultCode);
    }

}
