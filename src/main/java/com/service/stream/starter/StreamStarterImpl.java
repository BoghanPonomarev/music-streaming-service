package com.service.stream.starter;

import com.service.context.StreamContext;
import com.service.context.StreamContextImpl;
import com.service.dao.StreamRepository;
import com.service.entity.model.Stream;
import com.service.entity.StreamPortion;
import com.service.entity.enums.StreamStatusConst;
import com.service.exception.EntityNotFoundException;
import com.service.parser.Parser;
import com.service.stream.compile.StreamCompiler;
import com.service.system.FileReader;
import com.service.system.SystemResourceCleaner;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Queue;

@Component
@RequiredArgsConstructor
public class StreamStarterImpl implements StreamStarter{

    private static final String GLOBAL_STREAM_FOLDER_PATH = "src/main/resources/stream-source/";


    private final FileReader fileReader;


    private final StreamCompiler streamCompiler;

    private final Parser<String, Queue<StreamPortion>> parser;

    private final SystemResourceCleaner<String> stringSystemResourceCleaner;
    private final SystemResourceCleaner<Collection<StreamPortion>> systemResourceCleaner;

    private final StreamRepository streamRepository;

    @Override
    @Transactional
    public StreamContext startStream(String streamName) {
        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));

        StreamContextImpl newStreamContext = new StreamContextImpl(targetStream.getName(), 1, this, systemResourceCleaner);
        String commonDirectoryFilePath = GLOBAL_STREAM_FOLDER_PATH + streamName + "/1";

        String playListFile = commonDirectoryFilePath + "/" + streamName + ".m3u8";
        String playlistText = fileReader.readFile(playListFile);
        Queue<StreamPortion> parse = parser.parse(playlistText);
        parse.forEach(portion -> portion.setFilePath(commonDirectoryFilePath + "/" +  portion.getFilePath()));
        newStreamContext.appendStreamPortions(parse);
        targetStream.setCompilationIteration(1L);
        targetStream.setStreamStatusId(StreamStatusConst.PLAYING.getId());

        stringSystemResourceCleaner.cleanStreamResource(playListFile);
        newStreamContext.startStream();
        return newStreamContext;
    }

    @Override
    @Transactional
    public void compileNewPortion(StreamContext streamContext) {
        String streamName = streamContext.getStreamName();
        Thread thread = new Thread(() -> {
            streamCompiler.iterateCompileStream(streamName);
            Stream targetStream = streamRepository.findByName(streamName)
                    .orElseThrow(() -> new EntityNotFoundException("No such stream"));

            String commonDirectoryFilePath = GLOBAL_STREAM_FOLDER_PATH + targetStream.getName() + "/" + targetStream.getCompilationIteration();

            String playListFile = commonDirectoryFilePath + "/" + targetStream.getName() + ".m3u8";
            String playlistText = fileReader.readFile(playListFile);
            Queue<StreamPortion> parse = parser.parse(playlistText);
            parse.forEach(portion -> portion.setFilePath(commonDirectoryFilePath + "/" + portion.getFilePath()));
            streamContext.appendStreamPortions(parse);
            stringSystemResourceCleaner.cleanStreamResource(playListFile);
        });
        thread.start();
    }

}
