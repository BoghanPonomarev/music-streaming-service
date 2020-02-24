package com.service.web.rest;

import com.service.service.StreamManagementService;
import com.service.web.dto.ResourceCreationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
public class StreamManagementController {

  private StreamManagementService streamManagementService;

  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/playlist/{streamName}",consumes = "application/json", produces = "application/json")
  public ResponseEntity<ResourceCreationResponse<Long>> createStream(@PathVariable("streamName") String streamName) {
    Long newStreamId = streamManagementService.createStream(streamName);
    return ResponseEntity.ok(new ResourceCreationResponse<>(newStreamId));
  }

  @ResponseBody
  @PutMapping(value = "/playlist/{streamName}/video", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResourceCreationResponse<Long>> saveVideo(@PathVariable("streamName") String streamName,
                                                                  @RequestPart("video") MultipartFile video) throws IOException {
    Long newVideoId = streamManagementService.updateVideo(streamName, video.getInputStream(), video.getOriginalFilename());
    return ResponseEntity.ok(new ResourceCreationResponse<>(newVideoId));
  }

  @ResponseBody
  @PostMapping(value = "/playlist/{streamName}/audio", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResourceCreationResponse<Long>> addAudio(@PathVariable("streamName") String streamName,
                                                                  @RequestPart("audio") MultipartFile video) throws IOException {
    Long newAudioId = streamManagementService.addAudioFile(streamName, video.getInputStream(), video.getOriginalFilename());
    return ResponseEntity.ok(new ResourceCreationResponse<>(newAudioId));
  }


  @ResponseBody
  @DeleteMapping(value = "/playlist/audio/{audioId}", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<String> addAudio(@PathVariable("audioId") Long audioId) {
    streamManagementService.deleteAudioFile(audioId);
    return ResponseEntity.ok("OK");
  }

}
