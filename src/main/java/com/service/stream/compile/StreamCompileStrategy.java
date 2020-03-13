package com.service.stream.compile;

import com.service.entity.model.Stream;

public interface StreamCompileStrategy {

    void compileStream(String streamName);

    void iterateCompileStream(String streamName);
}
