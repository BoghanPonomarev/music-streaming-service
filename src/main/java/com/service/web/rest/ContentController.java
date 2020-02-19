package com.service.web.rest;

import com.service.context.StreamContentContext;
import com.service.parser.StreamUnit;
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

  private ResponseBuilder<String, StreamUnit> playlistResponseBuilder;
  private StreamContentContext streamContentContext;
  private StreamService streamService;

  @Autowired
  public ContentController(ResponseBuilder<String, StreamUnit> playlistResponseBuilder, StreamContentContext streamContentContext, StreamService streamService) {
    this.playlistResponseBuilder = playlistResponseBuilder;
    this.streamContentContext = streamContentContext;
    this.streamService = streamService;
    streamService.proceedStream("src/main/resources/static/1/result-stream.m3u8");
    streamService.proceedStream("src/main/resources/static/2/result-stream.m3u8");
  }

  @GetMapping(value = "/playlist", produces = "application/vnd.apple.mpegurl")
  public ResponseEntity<String> getPlaylist() {
    return ResponseEntity.ok(playlistResponseBuilder.buildResponse(streamContentContext.getCurrentStreamUnit()));
  }

  @GetMapping(value = "/ts/{id}", produces = "application/octet-stream")
  public ResponseEntity<FileSystemResource> ts(@PathVariable("id") Long id) {
    File file = new File(streamContentContext.getStreamUnit(id).getFilePath());
    return ResponseEntity.ok(new FileSystemResource(file));
  }

}
