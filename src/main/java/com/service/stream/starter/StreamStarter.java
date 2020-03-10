package com.service.stream.starter;

import com.service.context.StreamContext;

public interface StreamStarter {

    StreamContext startStream(String streamName);

}
