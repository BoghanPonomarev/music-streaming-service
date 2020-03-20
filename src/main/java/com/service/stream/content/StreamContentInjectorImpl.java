package com.service.stream.content;

import com.service.stream.context.StreamContext;
import com.service.dao.StreamRepository;
import com.service.entity.model.Stream;
import com.service.entity.StreamPortion;
import com.service.exception.EntityNotFoundException;
import com.service.stream.holder.StreamContextHolder;
import com.service.stream.parser.PlaylistParser;
import com.service.stream.compile.StreamCompiler;
import com.service.system.FileReader;
import com.service.system.SystemResourceCleaner;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamContentInjectorImpl implements StreamContentInjector {

    private static final String GLOBAL_STREAM_FOLDER_PATH = "src/main/resources/stream-source/";


    private final FileReader fileReader;


    private final StreamCompiler streamCompiler;

    private final PlaylistParser<String, Queue<StreamPortion>> playlistParser;

    private final SystemResourceCleaner<String> stringSystemResourceCleaner;

    private final StreamRepository streamRepository;

    @Override
    @Transactional
    public void injectStreamContent(String streamName) {
        StreamContext targetStreamContext = StreamContextHolder.getStreamContext(streamName);
        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));

        int nextCompilationIteration = targetStream.getLastCompilationIteration() + 1;
        streamCompiler.compileStream(targetStream);
        appendNewPortions(targetStreamContext, nextCompilationIteration);
    }

    private void appendNewPortions(StreamContext streamContext, int targetCompilationIteration) {
        String targetStreamName = streamContext.getStreamName();
        String commonDirectoryFilePath = GLOBAL_STREAM_FOLDER_PATH + targetStreamName + "/" + targetCompilationIteration;

        String playListFile = commonDirectoryFilePath + "/" + targetStreamName + ".m3u8";
        String playlistText = fileReader.readFile(playListFile);
        Queue<StreamPortion> parse = playlistParser.parse(playlistText);
        parse.forEach(portion -> portion.setFilePath(commonDirectoryFilePath + "/" + portion.getFilePath()));
        streamContext.appendStreamPortions(parse);
        stringSystemResourceCleaner.cleanStreamResource(playListFile);
    }
}