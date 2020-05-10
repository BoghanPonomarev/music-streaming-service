package com.service.stream.content;

public interface StreamContentInjector {

    void injectStreamContent(String streamName, boolean isFullRecompile, boolean isOnlyTsRecompilation);

}
