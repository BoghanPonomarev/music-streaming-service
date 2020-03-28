package com.service.stream.content;

import com.service.stream.context.StreamContext;
import com.service.dao.StreamRepository;
import com.service.entity.model.Stream;
import com.service.stream.context.StreamPortion;
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

    @Override //TODO now without transaction cause db interrupts long connection
    public void injectStreamContent(String streamName, boolean fullRecompile) {
        long start = System.currentTimeMillis();
        StreamContext targetStreamContext = StreamContextHolder.getStreamContext(streamName);
        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));

        int nextCompilationIteration = getNextStreamIteration(targetStream);
        streamCompiler.compileStream(targetStream, fullRecompile);
        appendNewPortions(targetStreamContext, nextCompilationIteration);
        log.info("Seconds spent for injection - {}",  TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));
    }

    private Integer getNextStreamIteration(Stream stream) {
        Integer compilationIteration = stream.getLastCompilationIteration();
        return compilationIteration != null ? compilationIteration + 1 : 1;
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
