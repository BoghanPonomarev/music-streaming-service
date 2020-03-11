package com.service.entity;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class TerminalCommand {

  private String command;

  private String commandWordPath;
  private String firstInputFile;
  private String secondInputFile;
  private String outputFile;


  public TerminalCommand(String command, String commandWordPath) {
    this.command = command;
    this.commandWordPath = commandWordPath;
  }

  public String toCommandTextLine() {
    String filledTextCommand;

    if (secondInputFile != null) {
      filledTextCommand = String.format(command, commandWordPath, firstInputFile, secondInputFile, outputFile);
    } else {
      filledTextCommand = String.format(command, commandWordPath, firstInputFile, outputFile);
    }

    return filledTextCommand;
  }

}
