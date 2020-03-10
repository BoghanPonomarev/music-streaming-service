package com.service.service;

import com.service.context.StreamContext;

public interface StreamManagementService {

  StreamContext getStreamContext(String streamName);

  void startStream(String streamName);

  void compileStream(String streamName);

}
