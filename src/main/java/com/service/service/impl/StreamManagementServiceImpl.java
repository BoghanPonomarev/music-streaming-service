package com.service.service.impl;

import com.service.stream.context.StreamContext;
import com.service.stream.context.impl.StreamContextImpl;
import com.service.dao.PlaylistRepository;
import com.service.dao.StreamRepository;
import com.service.entity.enums.StreamStatusConst;
import com.service.entity.model.Playlist;
import com.service.entity.model.Stream;
import com.service.exception.EntityNotFoundException;
import com.service.stream.holder.StreamContextHolder;
import com.service.service.StreamManagementService;
import com.service.stream.content.StreamContentInjector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamManagementServiceImpl implements StreamManagementService { //TODO Refactoring

    private static final String STREAM_SOURCES_FILE_PATH = "src/main/resources/stream-source";

    private final StreamContentInjector streamContentInjector;

    private final PlaylistRepository playlistRepository;
    private final StreamRepository streamRepository;

    @Override
    @Transactional
    public Long createStream(String streamName) {
        String noSpaceStreamName = streamName.replace(" ", "-");
        Playlist newPlaylist = new Playlist();
        playlistRepository.save(newPlaylist);

        Stream newStream = new Stream();
        newStream.setName(noSpaceStreamName);
        newStream.setTitle(streamName);
        newStream.setPlaylistId(newPlaylist.getId());
        newStream.setStreamStatusId(StreamStatusConst.CREATED.getId());

        Stream savedStream = streamRepository.save(newStream);
        createStreamDirectory(noSpaceStreamName);

        StreamContext streamContext = getOrCreateStreamContext(noSpaceStreamName);
        StreamContextHolder.addStreamContext(noSpaceStreamName, streamContext);
        return savedStream.getId();
    }

    private void createStreamDirectory(String streamName) {
        try {
            Files.createDirectories(Paths.get(STREAM_SOURCES_FILE_PATH + "/" + streamName));
        } catch (IOException ex) {
            log.error("Stream directory creation failed", ex);
        }
    }

    @Override
    public StreamContext getStreamContext(String streamName) {
        return StreamContextHolder.getStreamContext(streamName);
    }

    @Override
    public void startStream(String streamName) {
        StreamContext streamContext = StreamContextHolder.getStreamContext(streamName);
        streamContentInjector.injectStreamContent(streamName, false, false);

        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));
        targetStream.setStreamStatusId(StreamStatusConst.PLAYING.getId());
        streamRepository.save(targetStream);
        streamContext.startStream();
    }

    private StreamContext getOrCreateStreamContext(String streamName) {
        StreamContext targetStreamContext = StreamContextHolder.getStreamContext(streamName);
        if (targetStreamContext == null) {
            return new StreamContextImpl(streamName, streamContentInjector);
        }
        return targetStreamContext;
    }

    @Override
    public void compileStream(String streamName, boolean isOnlyTsRecompilation) {
        streamContentInjector.injectStreamContent(streamName, true, isOnlyTsRecompilation);

        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));

        if(targetStream.getStreamStatusId() == StreamStatusConst.CREATED.getId()) {
            targetStream.setStreamStatusId(StreamStatusConst.COMPILED.getId());
        }
        streamRepository.save(targetStream);
    }

    @Override
    @Transactional
    public void deleteStream(String streamName) throws IOException {
        Stream targetStream = streamRepository.findByName(streamName)
                .orElseThrow(() -> new EntityNotFoundException("No such stream"));

        playlistRepository.deleteById(targetStream.getPlaylistId());
        streamRepository.delete(targetStream);
        StreamContextHolder.removeStreamContext(streamName);
        FileUtils.deleteDirectory(new File(STREAM_SOURCES_FILE_PATH + "/" + streamName));
    }

}
