package com.service.service;

import com.service.context.StreamContext;

public interface StreamManagementService {

  Long createStream(String streamName);

  StreamContext getStreamContext(String streamName);

  void startStream(String streamName);

  void compileStream(String streamName);

}
