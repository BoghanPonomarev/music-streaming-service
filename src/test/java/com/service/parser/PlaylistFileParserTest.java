package com.service.parser;

import com.service.file.FileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Queue;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaylistFileParserTest {

  @Autowired
  private FileReader fileReader;

  @Test
  public void isCorrect() {
    PlaylistFileParser playlistFileParser = new PlaylistFileParser();

    Queue<StreamUnit> parse = playlistFileParser.parse(fileReader.readFile("src/test/resources/result-stream.m3u8"));

    Assertions.assertEquals(26, parse.size());
  }
}