package com.service.service;

import com.service.context.StreamContext;
import com.service.file.FileReader;
import com.service.file.SystemResourceCleaner;
import com.service.parser.Parser;
import com.service.parser.StreamPortion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
@RequiredArgsConstructor
public class StreamService {

  private final FileReader fileReader;
  private final StreamContext streamContext;
  private final Parser<Queue<StreamPortion>, String> parser;
  private final SystemResourceCleaner<String> stringSystemResourceCleaner;

  public void proceedStream(String playListFilePath) {
    String commonDirectoryFilePath = playListFilePath.substring(0, playListFilePath.lastIndexOf("/") + 1);

    String playlistText = fileReader.readFile(playListFilePath);
    Queue<StreamPortion> parse = parser.parse(playlistText);
    parse.forEach(portion -> portion.setFilePath(commonDirectoryFilePath + portion.getFilePath()));
    streamContext.appendStreamPortions(parse);
    streamContext.startStream();
    stringSystemResourceCleaner.cleanStreamResource(playListFilePath);
  }

}
