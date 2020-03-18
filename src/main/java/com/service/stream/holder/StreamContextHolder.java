package com.service.stream.holder;

import com.service.stream.context.StreamContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class StreamContextHolder {

    private static Map<String, StreamContext> streamContextMap = new ConcurrentHashMap<>();

    public static StreamContext getStreamContext(String streamName) {
        return streamContextMap.get(streamName);
    }

    public static void addStreamContext(String streamName, StreamContext streamContext) {
        streamContextMap.put(streamName, streamContext);
        log.info("New stream with name {} was added to general context", streamName);
    }

    public static void removeStreamContext(String streamName) {
        streamContextMap.remove(streamName);
        log.info("Stream with name {} was removed from general context", streamName);
    }

}
