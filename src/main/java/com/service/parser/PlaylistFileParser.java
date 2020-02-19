package com.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PlaylistFileParser implements Parser<Queue<StreamUnit>, String> {

  private Pattern transportStreamChunkPattern = Pattern.compile("#EXTINF:(\\d+\\.?\\d+),(.+?\\.ts)");

  @Override
  public Queue<StreamUnit> parse(String playlistText) {
    Queue<StreamUnit> streamUnitQueue = new LinkedList<>();

    Matcher transportStreamChunkMatcher = transportStreamChunkPattern.matcher(playlistText);
    while (transportStreamChunkMatcher.find()) {
      StreamUnit extractedStreamUnit = extractTransportStreamUnit(transportStreamChunkMatcher);
      streamUnitQueue.add(extractedStreamUnit);
    }

    return streamUnitQueue;
  }

  private StreamUnit extractTransportStreamUnit(Matcher transportStreamChunkMatcher) {
    String duration = transportStreamChunkMatcher.group(1);
    String filePath = transportStreamChunkMatcher.group(2);

    StreamUnit resultStreamUnit = new StreamUnit();
    resultStreamUnit.setDuration(Double.valueOf(duration));
    resultStreamUnit.setFilePath(filePath);
    return resultStreamUnit;
  }

}
