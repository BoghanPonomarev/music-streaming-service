package com.service.stream.compile;

import com.service.dao.AudioRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.entity.model.Audio;
import com.service.entity.model.Stream;
import com.service.entity.model.Video;
import com.service.stream.generation.StreamFilesGenerationChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StreamCompilerImpl implements StreamCompiler {

    private final StreamFilesGenerationChain streamCompileChain;
    private final StreamRepository streamRepository;
    private final VideoRepository videoRepository;
    private final AudioRepository audioRepository;

    @Override
    public void compileStream(Stream targetStream) {
        Integer newCompilationIteration = getNextStreamIteration(targetStream);
        StreamCompileContext streamCompileContext = buildStreamCompileContext(targetStream, newCompilationIteration);

        streamCompileChain.startAssembleStreamFiles(streamCompileContext);

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
}
