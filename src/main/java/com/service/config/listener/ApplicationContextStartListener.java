package com.service.config.listener;

import com.service.stream.context.impl.StreamContextImpl;
import com.service.entity.enums.StreamStatusConst;
import com.service.entity.model.Stream;
import com.service.stream.holder.StreamContextHolder;
import com.service.stream.content.StreamContentInjector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationContextStartListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final String GLOBAL_STREAM_FOLDER_PATH = "src/main/resources/stream-source/";

    private final JpaRepository<Stream, Long> streamRepository;

    private final StreamContentInjector streamContentInjector;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextStartedEvent) {
        List<Stream> streamList = streamRepository.findAll();
        streamList.stream()
                .peek(stream -> stream.setStreamStatusId(StreamStatusConst.CREATED.getId()))
                .peek(this::createStreamContext)
                .forEach(this::createStreamFolderIfNotExists);

        streamRepository.saveAll(streamList);
    }

    private void createStreamContext(Stream stream) {
        String streamName = stream.getName();
        StreamContextImpl newStreamContext = new StreamContextImpl(streamName, streamContentInjector);

        StreamContextHolder.addStreamContext(streamName, newStreamContext);
    }

    private void createStreamFolderIfNotExists(Stream stream) {
        String streamName = stream.getName();
        File streamDirectory = new File(GLOBAL_STREAM_FOLDER_PATH + "/" + streamName);
        if(!streamDirectory.exists()) {
          createDirectory(streamDirectory, streamName);
        }
    }

    private void createDirectory(File streamDirectory, String streamName) {
        boolean isDirectoryCreated = streamDirectory.mkdir();
        if(isDirectoryCreated) {
            log.info("New directory was created for stream with name {}", streamName);
        }
    }

}
