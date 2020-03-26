package com.service.stream.generation.chain;

import com.service.stream.compile.StreamCompileContext;

public interface StreamFilesGenerationChain {

  String continueAssembleStreamFiles(String firstAssembleSourceFilePath, String secondAssembleSourceFilePath, StreamCompileContext streamCompileContext);

  String startAssembleStreamFiles(StreamCompileContext streamCompileContext);

}
