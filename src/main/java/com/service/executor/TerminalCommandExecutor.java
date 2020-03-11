package com.service.executor;

import com.service.entity.TerminalCommand;
import com.service.exception.CommandExecutionException;

public interface TerminalCommandExecutor {

    void execute(TerminalCommand terminalCommand) throws CommandExecutionException;

}
