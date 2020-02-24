package com.service.web.rest;

import com.service.context.StreamContext;
import com.service.entity.StreamPortion;
import com.service.service.StreamService;
import com.service.web.builder.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class ContentController {

  private ResponseBuilder<String, StreamPortion> playlistResponseBuilder;
  private StreamContext streamContext;
  private StreamService streamService;

  @Autowired
  public ContentController(ResponseBuilder<String, StreamPortion> playlistResponseBuilder, StreamContext streamContext, StreamService streamService) {
    this.playlistResponseBuilder = playlistResponseBuilder;
    this.streamContext = streamContext;
    this.streamService = streamService;
    streamService.proceedStream("src/main/resources/stream-segment/0/result-stream.m3u8");
    streamService.proceedStream("src/main/resources/stream-segment/00/result-stream.m3u8");
  }

  @GetMapping(value = "/playlist", produces = "application/vnd.apple.mpegurl")
  public ResponseEntity<String> getPlaylist() {
    return ResponseEntity.ok(playlistResponseBuilder.buildResponse(streamContext.getCurrentStreamPortion()));
  }

  @GetMapping(value = "/ts/{id}", produces = "application/octet-stream")
  public ResponseEntity<FileSystemResource> ts(@PathVariable("id") Long id) {
    File file = new File(streamContext.getStreamPortion(id).getFilePath());
    return ResponseEntity.ok(new FileSystemResource(file));
  }

}
