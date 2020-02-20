package com.service.service;

import com.service.context.StreamContext;
import com.service.file.FileReader;
import com.service.parser.Parser;
import com.service.parser.StreamPortion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
public class StreamService {

  @Autowired
  private StreamContext streamContext;
  @Autowired
  private Parser<Queue<StreamPortion>, String> parser;
  @Autowired
  private FileReader fileReader;

  public void proceedStream(String filePath) {
    String incompleteFilePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);

    String playlistText = fileReader.readFile(filePath);
    Queue<StreamPortion> parse = parser.parse(playlistText);
    parse.forEach(portion -> portion.setFilePath(incompleteFilePath + portion.getFilePath()));
    streamContext.appendStreamPortions(parse);
    streamContext.startStream();
  }

}
