package com.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PlaylistFileParser implements Parser<Queue<TransportStreamUnit>, String> {

  private Pattern transportStreamChunkPattern = Pattern.compile("#EXTINF:(\\d+.?\\d+),\n" +
          "(.+)");

  @Override
  public Queue<TransportStreamUnit> parse(String playlistText) {
    Queue<TransportStreamUnit> transportStreamUnitQueue = new LinkedList<>();

    Matcher transportStreamChunkMatcher = transportStreamChunkPattern.matcher(playlistText);
    while (transportStreamChunkMatcher.find()) {
      TransportStreamUnit extractedTransportStreamUnit = extractTransportStreamUnit(transportStreamChunkMatcher);
      transportStreamUnitQueue.add(extractedTransportStreamUnit);
    }

    return transportStreamUnitQueue;
  }

  private TransportStreamUnit extractTransportStreamUnit(Matcher transportStreamChunkMatcher) {
    String duration = transportStreamChunkMatcher.group(1);
    String filePath = transportStreamChunkMatcher.group(2);

    return new TransportStreamUnit(filePath, duration);
  }

/*      try {
    List<String> playlistText = Files.readAllLines(Paths.get(parameter.getCanonicalPath()));
  } catch (IOException ex) {
    log.error("Failed during file reading",ex);
    //TODO throw custom ex
  }*/

}
