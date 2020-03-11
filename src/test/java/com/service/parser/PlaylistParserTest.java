package com.service.parser;

import com.service.entity.StreamPortion;
import com.service.system.FileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Queue;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaylistParserTest {

  @Autowired
  private FileReader fileReader;

  @Test
  public void isCorrect() {
    PlaylistParser playlistParser = new PlaylistParser();

    Queue<StreamPortion> parse = playlistParser.parse(fileReader.readFile("src/test/resources/result-stream.m3u8"));

    Assertions.assertEquals(26, parse.size());
  }
}