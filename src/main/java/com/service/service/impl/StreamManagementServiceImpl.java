package com.service.service.impl;

import com.service.context.StreamContext;
import com.service.dao.PlaylistRepository;
import com.service.dao.StreamRepository;
import com.service.entity.StreamPortion;
import com.service.entity.enums.StreamStatusConst;
import com.service.entity.model.Playlist;
import com.service.entity.model.Stream;
import com.service.service.StreamManagementService;
import com.service.stream.compile.StreamCompileStrategy;
import com.service.stream.starter.StreamStarter;
import com.service.system.SystemResourceCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamManagementServiceImpl implements StreamManagementService {

  private static final String STREAM_SOURCES_FILE_PATH = "src/main/resources/stream-source";

  private Map<String, StreamContext> streamContextMap = new HashMap<>();
  private final SystemResourceCleaner<Collection<StreamPortion>> systemResourceCleaner;
  private final StreamCompileStrategy streamCompiler;
  private final StreamStarter streamStarter;

  private final PlaylistRepository playlistRepository;
  private final StreamRepository streamRepository;

  @Override
  @Transactional
  public Long createStream(String streamName) {
    Playlist newPlaylist = new Playlist();
    playlistRepository.save(newPlaylist);

    Stream newStream = new Stream();
    newStream.setName(streamName);
    newStream.setPlaylistId(newPlaylist.getId());
    newStream.setStreamStatusId(StreamStatusConst.CREATED.getId());

    Stream savedStream = streamRepository.save(newStream);
    createStreamDirectory(streamName);
    return savedStream.getId();
  }

  private void createStreamDirectory(String streamName) {
    try {
      Files.createDirectory(Paths.get(STREAM_SOURCES_FILE_PATH + "/" + streamName));
    } catch (IOException ex) {
      log.error("Stream directory creation failed", ex);
    }
  }

  @Override
  public StreamContext getStreamContext(String streamName) {
    return streamContextMap.get(streamName);
  }

  @Override
  public void startStream(String streamName) {
    StreamContext newStreamContext = streamStarter.startStream(streamName);
    streamContextMap.put(streamName, newStreamContext);
    streamStarter.compileNewPortion(newStreamContext);
  }

  @Override
  public void compileStream(String streamName) {
    streamCompiler.compileStream(streamName);
  }

}
