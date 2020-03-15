package com.service.system.impl;

import com.service.entity.StreamPortion;
import com.service.system.SystemResourceCleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class AsyncSystemResourceCleanerImpl implements SystemResourceCleaner<Collection<StreamPortion>> {

  private ExecutorService cleanExecutorService = Executors.newSingleThreadExecutor();

  @Override
  public void cleanStreamResource(Collection<StreamPortion> resources) {
    cleanExecutorService.execute(() -> cleanResourceSynchronously(resources));
  }

  private void cleanResourceSynchronously(Collection<StreamPortion> resources) {
    String potentialCommonDirectoryFilePath = extractPotentialCommonDirectoryPath(resources);

    if (potentialCommonDirectoryFilePath != null && isCommonDirectory(resources, potentialCommonDirectoryFilePath)) {
      deleteDirectory(potentialCommonDirectoryFilePath);
      log.info("Directory path - {} was deleted", potentialCommonDirectoryFilePath);
    } else {
      resources.forEach(this::cleanSingleStreamPortion);
    }
  }

  private String extractPotentialCommonDirectoryPath(Collection<StreamPortion> resources) {
    Iterator<StreamPortion> iterator = resources.iterator();

    if (iterator.hasNext()) {
      StreamPortion firstPortion = iterator.next();
      String firstPortionFilePath = firstPortion.getFilePath();
      return firstPortionFilePath.substring(0, firstPortionFilePath.lastIndexOf("/"));
    }
    return null;
  }

  private boolean isCommonDirectory(Collection<StreamPortion> resources, String potentialCommonDirectoryFilePath) {
    boolean isResourcesInCommonDirectory = resources.stream()
            .allMatch(streamPortion -> streamPortion.getFilePath().contains(potentialCommonDirectoryFilePath));

    File commonDirectory = new File(potentialCommonDirectoryFilePath);
    String[] commonDirectoryFiles = commonDirectory.list();
    return isResourcesInCommonDirectory && commonDirectoryFiles != null && commonDirectoryFiles.length == resources.size();
  }

  private void deleteDirectory(String directoryFilePath) {
    try {
      FileUtils.deleteDirectory(new File(directoryFilePath));
    } catch (IOException e) {
      log.error("Failed during directory removing, file path - {}", directoryFilePath);
    }
  }

  private void cleanSingleStreamPortion(StreamPortion streamPortion) {
    try {
      File streamPortionFile = new File(streamPortion.getFilePath());
      FileUtils.forceDelete(streamPortionFile);
      log.info("File with path - {} was deleted", streamPortionFile.getCanonicalPath());
    } catch (IOException e) {
      log.error("Failed during file removing, stream part - {}", streamPortion);
    }
  }

}
