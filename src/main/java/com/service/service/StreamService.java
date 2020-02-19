package com.service.service;

import com.service.context.StreamContentContext;
import com.service.file.FileReader;
import com.service.parser.Parser;
import com.service.parser.StreamUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Service
public class StreamService {

  @Autowired
  private StreamContentContext streamContentContext;
  @Autowired
  private Parser<Queue<StreamUnit>, String> parser;
  @Autowired
  private FileReader fileReader;

  public void proceedStream(String filePath) {
    String incompleteFilePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);

    String playlistText = fileReader.readFile(filePath);
    Queue<StreamUnit> parse = parser.parse(playlistText);
    parse.forEach(unit -> unit.setFilePath(incompleteFilePath + unit.getFilePath()));
    streamContentContext.addStreamUnits(parse);
    streamContentContext.startStream();
  }

}
