package com.service.service;

import com.service.context.StreamContext;
import com.service.dao.AudioRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.entity.Audio;
import com.service.entity.Stream;
import com.service.entity.Video;
import com.service.entity.enums.StreamStatusConst;
import com.service.exception.EntityNotFoundException;
import com.service.exception.IllegalStreamStateException;
import com.service.service.strategy.StreamCompileContext;
import com.service.service.strategy.StreamCompileStrategy;
import com.service.system.FileReader;
import com.service.system.SystemResourceCleaner;
import com.service.parser.Parser;
import com.service.entity.StreamPortion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StreamManagementServiceImpl implements StreamManagementService {

  private static final int START_STREAM_COMPILATION_ITERATION = 1;

  private final FileReader fileReader;
  private final StreamContext streamContext;
  private final Parser<String, Queue<StreamPortion>> parser;
  private final SystemResourceCleaner<String> stringSystemResourceCleaner;

  private final StreamCompileStrategy streamCompileStrategy;
  private final StreamRepository streamRepository;
  private final VideoRepository videoRepository;
  private final AudioRepository audioRepository;

  public void proceedStream(String playListFilePath) {
    String commonDirectoryFilePath = playListFilePath.substring(0, playListFilePath.lastIndexOf("/") + 1);

    String playlistText = fileReader.readFile(playListFilePath);
    Queue<StreamPortion> parse = parser.parse(playlistText);
    parse.forEach(portion -> portion.setFilePath(commonDirectoryFilePath + portion.getFilePath()));
    streamContext.appendStreamPortions(parse);
    streamContext.startStream();
    stringSystemResourceCleaner.cleanStreamResource(playListFilePath);
  }


  @Override
  public void compileStream(String streamName) {
    Stream targetStream = getStreamWithStatusCheck(streamName, StreamStatusConst.CREATED);

    List<Video> streamVideos = videoRepository.findAllByPlaylistId(targetStream.getPlaylistId());
    List<String> streamAudiosPathList = audioRepository.findAllByPlaylistId(targetStream.getPlaylistId())
            .stream().map(Audio::getFilePath).collect(Collectors.toList());

    StreamCompileContext streamCompileContext = new StreamCompileContext(START_STREAM_COMPILATION_ITERATION, streamName, streamVideos.get(0).getFilePath(), streamAudiosPathList);
    streamCompileStrategy.compileStream(streamCompileContext);

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
