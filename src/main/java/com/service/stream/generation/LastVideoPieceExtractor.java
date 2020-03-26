package com.service.stream.generation;

import com.service.stream.compile.StreamCompileContext;

public interface LastVideoPieceExtractor {

    String extractLastVideoPiece(String loopedVideoWithAudioTracks, StreamCompileContext streamCompileContext);

}
