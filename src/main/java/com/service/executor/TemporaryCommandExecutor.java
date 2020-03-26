package com.service.executor;

import com.service.entity.TerminalCommand;

public interface TemporaryCommandExecutor {

    String executeWithTemporaryResult(String firstParamFile, String secondParamFile,
                                      TerminalCommand targetCommand, String outFileExtension);

}
