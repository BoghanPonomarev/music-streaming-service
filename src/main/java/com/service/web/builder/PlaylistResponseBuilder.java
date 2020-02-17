package com.service.web.builder;

import com.service.StreamContentContext;
import org.springframework.stereotype.Component;

@Component
public class PlaylistResponseBuilder implements ResponseBuilder<String, StreamContentContext> {

  @Override
  public String buildResponse(StreamContentContext streamContentContext) {
    return "#EXTM3U\n" +
            "#EXT-X-VERSION:3\n" +
            "#EXT-X-TARGETDURATION:20\n" +
            "#EXT-X-MEDIA-SEQUENCE:"+ streamContentContext.getCurrentChunkId() +"\n" +
            "#EXTINF:" + streamContentContext.getCurrentChunkDuration() + ",\n" +
            "ts/" + streamContentContext.getCurrentChunkId();
  }

}
