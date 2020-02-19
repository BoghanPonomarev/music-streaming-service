package com.service.web.builder;

import com.service.parser.StreamUnit;
import org.springframework.stereotype.Component;

@Component
public class PlaylistResponseBuilder implements ResponseBuilder<String, StreamUnit> {

  @Override
  public String buildResponse(StreamUnit streamUnit) {
    String s2 = (streamUnit.getId() == 4) ? "#EXT-X-DISCONTINUITY\n" : "";

    if(streamUnit.getId() < 2) {
      return "#EXTM3U\n" +
              "#EXT-X-VERSION:3\n" +
              "#EXT-X-TARGETDURATION:20\n" +
              "#EXT-X-MEDIA-SEQUENCE:"+ streamUnit.getId() +"\n" +
              "#EXTINF:" + streamUnit.getDuration() + ",\n" +
              "ts/" + streamUnit.getId();
    }

    return "#EXTM3U\n" +
            "#EXT-X-VERSION:3\n" +
            "#EXT-X-TARGETDURATION:20\n" +
            "#EXT-X-MEDIA-SEQUENCE:"+ streamUnit.getId() +"\n" +
            "#EXTINF:" + streamUnit.getDuration() + ",\n" +
            "ts/" + (streamUnit.getId() - 1) + "\n" + s2 +
            "#EXTINF:" + streamUnit.getDuration() + ",\n" +
            "ts/" + streamUnit.getId();
  }

}
