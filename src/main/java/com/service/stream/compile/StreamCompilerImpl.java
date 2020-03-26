package com.service.stream.compile;

import com.service.dao.AudioRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.entity.model.Audio;
import com.service.entity.model.Stream;
import com.service.entity.model.Video;
import com.service.stream.generation.chain.StreamFilesGenerationChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StreamCompilerImpl implements StreamCompiler {

    private final StreamFilesGenerationChain streamCompileChain;

    private final StreamFilesGenerationChain streamPlaylistGenerationChain;

    private final StreamRepository streamRepository;
    private final VideoRepository videoRepository;
    private final AudioRepository audioRepository;

    public StreamCompilerImpl(@Qualifier("streamStartGenerationChainMember") StreamFilesGenerationChain streamCompileChain,
                              @Qualifier("streamPlaylistGenerationChainMember") StreamFilesGenerationChain streamPlaylistGenerationChain,
                              StreamRepository streamRepository, VideoRepository videoRepository, AudioRepository audioRepository) {
        this.streamCompileChain = streamCompileChain;
        this.streamPlaylistGenerationChain = streamPlaylistGenerationChain;
        this.streamRepository = streamRepository;
        this.videoRepository = videoRepository;
        this.audioRepository = audioRepository;
    }

    @Override
    public void compileStream(Stream targetStream, boolean fullRecompile) {
        Integer newCompilationIteration = getNextStreamIteration(targetStream);
        StreamCompileContext streamCompileContext = buildStreamCompileContext(targetStream, newCompilationIteration);

        assembleStream(streamCompileContext, fullRecompile);

        targetStream.setLastCompilationIteration(newCompilationIteration);
        streamRepository.save(targetStream);
    }

    private Integer getNextStreamIteration(Stream stream) {
        Integer compilationIteration = stream.getLastCompilationIteration();
        return compilationIteration != null ? compilationIteration + 1 : 1;
    }

    private StreamCompileContext buildStreamCompileContext(Stream stream, int newCompilationIteration) {
        List<String> streamVideosPaths = videoRepository.findAllByPlaylistId(stream.getPlaylistId())
                .stream().map(Video::getFilePath)
                .collect(Collectors.toList());
        List<String> streamAudiosPathList = audioRepository.findAllByPlaylistId(stream.getPlaylistId())
                .stream().map(Audio::getFilePath)
                .collect(Collectors.toList());

        return new StreamCompileContext(newCompilationIteration, stream.getName(), streamVideosPaths, streamAudiosPathList);
    }

    private void assembleStream(StreamCompileContext streamCompileContext, boolean fullRecompile) {
        String compiledPlaylistVideoPath = "src/main/resources/stream-source/" +
                streamCompileContext.getStreamName() + "/compiled-content.mp4";

        if (fullRecompile) {
            streamCompileChain.startAssembleStreamFiles(streamCompileContext);
        } else {
            streamPlaylistGenerationChain.continueAssembleStreamFiles(compiledPlaylistVideoPath, null, streamCompileContext);
        }
    }
}
