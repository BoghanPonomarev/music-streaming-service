package com.service.service;

import java.io.File;
import java.io.InputStream;

public interface StreamManagementService {

  Long createStream(String streamName);

  Long updateVideo(String streamName, InputStream videoInputStream, String originalFileName);

  Long addAudioFile(String streamName, InputStream audioInputStream, String originalFileName);

  void deleteAudioFile(Long audioId);

}
