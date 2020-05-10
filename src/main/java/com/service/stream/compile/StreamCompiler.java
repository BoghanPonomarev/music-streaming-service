package com.service.stream.compile;

import com.service.entity.model.Stream;

public interface StreamCompiler {

    void compileStream(Stream targetStream, boolean isFullRecompile, boolean isOnlyTsCompilation);

}
