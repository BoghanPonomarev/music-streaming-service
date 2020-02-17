package com.service.parser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Queue;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaylistFileParserTest {

  @Test
  public void isCorrect() {
    PlaylistFileParser playlistFileParser = new PlaylistFileParser();

    Queue<TransportStreamUnit> parse = playlistFileParser.parse("#EXTM3U\n" +
            "#EXT-X-VERSION:3\n" +
            "#EXT-X-TARGETDURATION:10\n" +
            "#EXT-X-MEDIA-SEQUENCE:0\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream0.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream1.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream2.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream3.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream4.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream5.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream6.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream7.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream8.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream9.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream10.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream11.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream12.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream13.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream14.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream15.ts\n" +
            "#EXTINF:10.416667,\n" +
            "result-stream16.ts\n" +
            "#EXTINF:7.500000,\n" +
            "result-stream17.ts\n" +
            "#EXT-X-ENDLIST\n");

    parse.forEach(System.out::println);
  }
}