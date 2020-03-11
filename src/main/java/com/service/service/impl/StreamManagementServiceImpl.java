package com.service.service.impl;

import com.service.context.StreamContext;
import com.service.service.StreamManagementService;
import com.service.stream.compile.StreamCompiler;
import com.service.stream.starter.StreamStarter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StreamManagementServiceImpl implements StreamManagementService {

  private Map<String, StreamContext> streamContextMap = new HashMap<>();
  private final StreamCompiler streamCompiler;
  private final StreamStarter streamStarter;

  @Override
  public StreamContext getStreamContext(String streamName) {
    return streamContextMap.get(streamName);
  }

  @Override
  public void startStream(String streamName) {
    StreamContext newStreamContext = streamStarter.startStream(streamName);
    streamContextMap.put(streamName, newStreamContext);
    streamCompiler.iterateCompileStream(streamName);
  }

  @Override
  public void compileStream(String streamName) {
    streamCompiler.startCompileStream(streamName);
  }

}
