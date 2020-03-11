package com.service.stream.starter;

import com.service.context.StreamContext;
import org.springframework.transaction.annotation.Transactional;

public interface StreamStarter {

    StreamContext startStream(String streamName);

    void compileNewPortion(StreamContext streamContext);
}
