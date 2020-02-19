package com.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileReaderImpl implements FileReader {

  @Override
  public String readFile(String filePath) {
    try {
     return String.join("", Files.readAllLines(Paths.get(filePath)));
    } catch (IOException ex) {
      log.error("Failed during file reading", ex);
      //TODO throw custom ex
    }
    return null;
  }


}
