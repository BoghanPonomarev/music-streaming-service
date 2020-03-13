package com.service.stream.compile;

import com.service.dao.AudioRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.entity.model.Audio;
import com.service.entity.model.Stream;
import com.service.entity.model.Video;
import com.service.entity.enums.StreamStatusConst;
import com.service.exception.EntityNotFoundException;
import com.service.exception.IllegalStreamStateException;
import com.service.stream.compile.assemble.StreamFilesGenerationChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StreamFirstCompileStrategyImpl implements StreamCompileStrategy {

    private static final int START_STREAM_COMPILATION_ITERATION = 1;

    private final StreamFilesGenerationChain streamCompileChain;
    private final StreamRepository streamRepository;
    private final VideoRepository videoRepository;
    private final AudioRepository audioRepository;

    @Override
    public void compileStream(String streamName) {
        Stream targetStream = getStreamWithStatusCheck(streamName, StreamStatusConst.CREATED);

        List<Video> streamVideos = videoRepository.findAllByPlaylistId(targetStream.getPlaylistId());
        List<String> streamAudiosPathList = audioRepository.findAllByPlaylistId(targetStream.getPlaylistId())
                .stream().map(Audio::getFilePath).collect(Collectors.toList());

        StreamCompileContext streamCompileContext = new StreamCompileContext(START_STREAM_COMPILATION_ITERATION, streamName, streamVideos.get(0).getFilePath(), streamAudiosPathList);
        streamCompileChain.startAssembleStreamFiles(streamCompileContext);

        targetStream.setStreamStatusId(StreamStatusConst.COMPILED.getId());
        streamRepository.save(targetStream);
    }

    private Stream getStreamWithStatusCheck(String streamName, StreamStatusConst supportedStatus) {
        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));

        if (targetStream.getStreamStatusId() != supportedStatus.getId()) {
            throw new IllegalStreamStateException("Stream in illegal state");
        }

        return targetStream;
    }

    @Override
    public void iterateCompileStream(String streamName) {
        Stream targetStream = getStreamWithStatusCheck(streamName, StreamStatusConst.PLAYING);

        List<Video> streamVideos = videoRepository.findAllByPlaylistId(targetStream.getPlaylistId());
        List<String> streamAudiosPathList = audioRepository.findAllByPlaylistId(targetStream.getPlaylistId())
                .stream().map(Audio::getFilePath).collect(Collectors.toList());

        long newCompilationIteration = targetStream.getCompilationIteration() + 1L;
        StreamCompileContext streamCompileContext = new StreamCompileContext((int) newCompilationIteration, streamName, streamVideos.get(0).getFilePath(), streamAudiosPathList);
        streamCompileChain.startAssembleStreamFiles(streamCompileContext);

        targetStream.setCompilationIteration(newCompilationIteration);
        streamRepository.save(targetStream);
    }


}
