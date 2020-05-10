package com.service.web.rest;

import com.service.service.StreamManagementService;
import com.service.web.dto.CompileStreamRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StreamManagementController {

  private final StreamManagementService streamManagementService;

  @ResponseBody
  @PostMapping(value = "/streams/{streamName}/compile", consumes = "application/json")
  public ResponseEntity<String> compileStream(@PathVariable("streamName") String streamName,
                                              @RequestBody CompileStreamRequestDto compileStreamRequestDto) {
    streamManagementService.compileStream(streamName, compileStreamRequestDto.getIsOnlyTsRecompilation());
    return ResponseEntity.ok("OK");
  }

  @ResponseBody
  @PostMapping(value = "/streams/{streamName}/play")
  public ResponseEntity<String> startStream(@PathVariable("streamName") String streamName) {
    streamManagementService.startStream(streamName);
    return ResponseEntity.ok("OK");
  }

  @ResponseBody
  @DeleteMapping(value = "/streams/{streamName}")
  public ResponseEntity<String> deleteStream(@PathVariable("streamName") String streamName) throws IOException {
    streamManagementService.deleteStream(streamName);
    return ResponseEntity.ok("OK");
  }

}
