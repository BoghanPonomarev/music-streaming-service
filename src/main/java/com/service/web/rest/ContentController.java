package com.service.web.rest;

import com.service.StreamContentContext;
import com.service.service.ContentServiceImpl;
import com.service.web.builder.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController

@RequestMapping(value = "/api/v1")
public class ContentController {

  private ResponseBuilder<String, StreamContentContext> playlistResponseBuilder;

  private StreamContentContext streamContentContext = new StreamContentContext();

  @Autowired
  public ContentController(ResponseBuilder<String, StreamContentContext> playlistResponseBuilder) {
    this.playlistResponseBuilder = playlistResponseBuilder;

     ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(() -> streamContentContext.setCurrentChunkId(streamContentContext.getCurrentChunkId() + 1), 10, 10, TimeUnit.SECONDS);
  }

  @GetMapping(value = "/playlist", produces = "application/vnd.apple.mpegurl")
  public ResponseEntity<String> getPlaylist()  {
    return ResponseEntity.ok(playlistResponseBuilder.buildResponse(streamContentContext));
  }

  @GetMapping(value = "/ts/{id}", produces = "application/octet-stream")
  public ResponseEntity<FileSystemResource> ts(@PathVariable("id") Integer id)  {
    File file;
     file = new File("src/main/resources/static/result-stream"+id+".ts");
    return ResponseEntity.ok(new FileSystemResource(file));
  }

}
