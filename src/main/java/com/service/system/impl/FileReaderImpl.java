package com.service.system.impl;

import com.service.exception.FileSystemOperationException;
import com.service.system.FileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class FileReaderImpl implements FileReader {

  @Override
  public String readFile(String filePath) {
    try {
      return String.join("", Files.readAllLines(Paths.get(filePath)));
    } catch (IOException ex) {
      log.error("Failed during file reading", ex);
      throw new FileSystemOperationException("Failed during file reading", ex);
    }
  }


}
