package com.service.config.listener;

import com.service.stream.context.StreamContextImpl;
import com.service.entity.StreamPortion;
import com.service.entity.enums.StreamStatusConst;
import com.service.entity.model.Stream;
import com.service.stream.holder.StreamContextHolder;
import com.service.stream.content.StreamContentInjector;
import com.service.system.SystemResourceCleaner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationContextStartListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final String GLOBAL_STREAM_FOLDER_PATH = "src/main/resources/stream-source/";

    private final JpaRepository<Stream, Long> streamRepository;

    private final SystemResourceCleaner<Collection<StreamPortion>> systemResourceCleaner;
    private final StreamContentInjector streamContentInjector;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextStartedEvent) {
        List<Stream> streamList = streamRepository.findAll();
        streamList.stream()
                .peek(stream -> stream.setStreamStatusId(StreamStatusConst.CREATED.getId()))
                .forEach(this::createStreamContext);

        streamRepository.saveAll(streamList);
    }

    private void createStreamContext(Stream stream) {
        String streamName = stream.getName();
        StreamContextImpl newStreamContext = new StreamContextImpl(streamName,
                extractLastCompilationIteration(stream), streamContentInjector, systemResourceCleaner);

        StreamContextHolder.addStreamContext(streamName, newStreamContext);
    }

    private void createStreamFolder(Stream stream) {

    }

    private long extractLastCompilationIteration(Stream stream) {
        Integer lastCompilationIteration = stream.getLastCompilationIteration();
        return lastCompilationIteration != null ? lastCompilationIteration : 1L;
    }

}
