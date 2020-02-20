package com.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PlaylistFileParser implements Parser<Queue<StreamPortion>, String> {

  private Pattern transportStreamChunkPattern = Pattern.compile("#EXTINF:(\\d+\\.?\\d+),(.+?\\.ts)");

  @Override
  public Queue<StreamPortion> parse(String playlistText) {
    Queue<StreamPortion> streamPortionQueue = new LinkedList<>();

    Matcher transportStreamChunkMatcher = transportStreamChunkPattern.matcher(playlistText);
    while (transportStreamChunkMatcher.find()) {
      StreamPortion extractedStreamPortion = extractTransportStreamPortion(transportStreamChunkMatcher);
      streamPortionQueue.add(extractedStreamPortion);
    }

    return streamPortionQueue;
  }

  private StreamPortion extractTransportStreamPortion(Matcher transportStreamChunkMatcher) {
    String duration = transportStreamChunkMatcher.group(1);
    String filePath = transportStreamChunkMatcher.group(2);

    StreamPortion resultStreamPortion = new StreamPortion();
    resultStreamPortion.setDuration(Double.valueOf(duration));
    resultStreamPortion.setFilePath(filePath);
    return resultStreamPortion;
  }

}
