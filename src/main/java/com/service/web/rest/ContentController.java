package com.service.web.rest;

import com.service.context.StreamContext;
import com.service.entity.StreamPortion;
import com.service.web.builder.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class ContentController {

  private ResponseBuilder<String, StreamPortion> playlistResponseBuilder;
  private StreamContext streamContext;

  @Autowired
  public ContentController(ResponseBuilder<String, StreamPortion> playlistResponseBuilder, StreamContext streamContext) {
    this.playlistResponseBuilder = playlistResponseBuilder;
    this.streamContext = streamContext;
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
