package com.service.entity;

import lombok.Data;

@Data
public class FileModificationCommand {

  private String command;

  private String commandWordPath;
  private String firstInputFile;
  private String secondInputFile;
  private String outputFile;


  public FileModificationCommand(String command, String commandWordPath) {
    this.command = command;
    this.commandWordPath = commandWordPath;
  }

}
