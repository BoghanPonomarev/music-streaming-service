package com.service.executor.impl;

import com.service.entity.TerminalCommand;
import com.service.executor.TemporaryCommandExecutor;
import com.service.executor.TerminalCommandExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TemporaryCommandExecutorImpl implements TemporaryCommandExecutor {

    private final TerminalCommandExecutor commandExecutor;

    public String executeWithTemporaryResult(String firstParamFile, String secondParamFile, TerminalCommand targetCommand, String outFileExtension) {
        String outputFileName = "src/main/resources/temp/" + UUID.randomUUID() + "." + outFileExtension;
        targetCommand.setOutputFile(outputFileName);
        targetCommand.setFirstInputFile(firstParamFile);
        targetCommand.setSecondInputFile(secondParamFile);

        commandExecutor.execute(targetCommand);
        return outputFileName;
    }

}
