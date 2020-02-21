package com.service.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class SingleFIleSystemResourceCleaner implements SystemResourceCleaner<String> {

  @Override
  public void cleanStreamResource(String filePath) {
    File fileToDelete = new File(filePath);
    try {
      FileUtils.forceDelete(fileToDelete);
    } catch (IOException e) {
      log.error("Failed during file removing, path - {}", filePath);
    }
  }

}
