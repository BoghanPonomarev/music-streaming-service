package com.service.config.listener;

import com.service.context.StreamContext;
import com.service.context.StreamContextImpl;
import com.service.entity.StreamPortion;
import com.service.entity.enums.StreamStatusConst;
import com.service.entity.model.Stream;
import com.service.holder.StreamContextHolder;
import com.service.stream.starter.StreamContentInjector;
import com.service.system.SystemResourceCleaner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationContextStartListener implements ApplicationListener<ContextRefreshedEvent> {

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

    private long extractLastCompilationIteration(Stream stream) {
        Integer lastCompilationIteration = stream.getLastCompilationIteration();
        return lastCompilationIteration != null ? lastCompilationIteration : 1L;
    }

}
