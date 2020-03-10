package com.service.web.rest;

import com.service.context.StreamContext;
import com.service.entity.StreamPortion;
import com.service.service.StreamManagementService;
import com.service.web.builder.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.File;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ContentController {

  private final ResponseBuilder<String, StreamPortion> playlistResponseBuilder;
  private final StreamManagementService streamManagementService;

  @GetMapping(value = "/streams/{streamName}/playlist", produces = "application/vnd.apple.mpegurl")
  public ResponseEntity<String> getPlaylist(@PathVariable("streamName") String streamName) {
    StreamContext streamContext = streamManagementService.getStreamContext(streamName);
    return ResponseEntity.ok(playlistResponseBuilder.buildResponse(streamContext.getCurrentStreamPortion()));
  }

  @GetMapping(value = "/streams/{streamName}/ts/{id}", produces = "application/octet-stream")
  public ResponseEntity<FileSystemResource> getTs(@PathVariable("streamName") String streamName, @PathVariable("id") Long tsId) {
    StreamContext streamContext = streamManagementService.getStreamContext(streamName);
    File file = new File(streamContext.getStreamPortion(tsId).getFilePath());
    return ResponseEntity.ok(new FileSystemResource(file));
  }

}
