package com.service.service;

import com.service.stream.context.StreamContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface StreamManagementService {

    Long createStream(String streamName);

    StreamContext getStreamContext(String streamName);

    void startStream(String streamName);

    void compileStream(String streamName);

    void deleteStream(String streamName) throws IOException;
}
