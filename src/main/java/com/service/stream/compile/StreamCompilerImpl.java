package com.service.stream.compile;

import com.service.dao.AudioRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.entity.Audio;
import com.service.entity.Stream;
import com.service.entity.Video;
import com.service.entity.enums.StreamStatusConst;
import com.service.exception.EntityNotFoundException;
import com.service.exception.IllegalStreamStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StreamCompilerImpl implements StreamCompiler {

    private static final int START_STREAM_COMPILATION_ITERATION = 1;

    private final StreamCompileChain streamCompileChain;
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
        streamCompileChain.compileStream(streamCompileContext);

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

}
