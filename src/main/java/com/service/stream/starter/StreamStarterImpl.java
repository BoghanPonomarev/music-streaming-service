package com.service.stream.starter;

import com.service.context.StreamContext;
import com.service.dao.StreamRepository;
import com.service.entity.Stream;
import com.service.entity.StreamPortion;
import com.service.entity.enums.StreamStatusConst;
import com.service.exception.EntityNotFoundException;
import com.service.parser.Parser;
import com.service.stream.compile.StreamCompiler;
import com.service.system.FileReader;
import com.service.system.SystemResourceCleaner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Queue;

@Component
public class StreamStarterImpl implements StreamStarter{

    private static final String GLOBAL_STREAM_FOLDER_PATH = "src/main/resources/stream-source/";

    @Autowired
    private  FileReader fileReader;
    @Lazy
    @Autowired
    private  StreamContext streamContext;
    @Autowired
    private  StreamCompiler streamCompiler;
    @Autowired
    private  Parser<String, Queue<StreamPortion>> parser;
    @Autowired
    private  SystemResourceCleaner<String> stringSystemResourceCleaner;
    @Autowired
    private  StreamRepository streamRepository;

    @Override
    @Transactional
    public StreamContext startStream(String streamName) {
        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));

        String commonDirectoryFilePath = GLOBAL_STREAM_FOLDER_PATH + streamName + "/1";

        String playlistText = fileReader.readFile(commonDirectoryFilePath + "/" + streamName + ".m3u8");
        Queue<StreamPortion> parse = parser.parse(playlistText);
        parse.forEach(portion -> portion.setFilePath(commonDirectoryFilePath + "/" +  portion.getFilePath()));
        streamContext.appendStreamPortions(parse);
        targetStream.setCompilationIteration(1L);
        targetStream.setStreamStatusId(StreamStatusConst.PLAYING.getId());

        streamContext.startStream();
        return streamContext;
                //        stringSystemResourceCleaner.cleanStreamResource(commonDirectoryFilePath);
    }

    @Override
    @Transactional
    public void continueStream() {
        Thread thread = new Thread(() -> {
            streamCompiler.iterateCompileStream("testStream");
            Stream targetStream = streamRepository.findByName("testStream")
                    .orElseThrow(() -> new EntityNotFoundException("No such stream"));

            String commonDirectoryFilePath = GLOBAL_STREAM_FOLDER_PATH + targetStream.getName() + "/" + targetStream.getCompilationIteration();

            String playlistText = fileReader.readFile(commonDirectoryFilePath + "/" + targetStream.getName() + ".m3u8");
            Queue<StreamPortion> parse = parser.parse(playlistText);
            parse.forEach(portion -> portion.setFilePath(commonDirectoryFilePath + "/" + portion.getFilePath()));
            streamContext.appendStreamPortions(parse);
        });
        thread.start();
    }

}
