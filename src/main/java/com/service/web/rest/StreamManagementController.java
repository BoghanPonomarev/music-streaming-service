package com.service.web.rest;

import com.service.service.StreamManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class StreamManagementController {

  private final StreamManagementService streamManagementService;

  @ResponseBody
  @PostMapping(value = "/stream/{streamName}/compile")
  public ResponseEntity<String> createPlaylist(@PathVariable("streamName") String streamName) {
    streamManagementService.compileStream(streamName);
    return ResponseEntity.ok("OK");
  }


}