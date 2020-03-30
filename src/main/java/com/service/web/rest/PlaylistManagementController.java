package com.service.web.rest;

import com.service.service.MediaService;
import com.service.service.PlaylistManagementService;
import com.service.service.StreamManagementService;
import com.service.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlaylistManagementController {

  private final PlaylistManagementService playlistManagementService;
  private final StreamManagementService streamManagementService;
  private final MediaService mediaService;

  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/streams/{streamName}", produces = "application/json")
  public ResponseEntity<ResourceCreationResponse<Long>> createPlaylist(@PathVariable("streamName") String streamName) {
    Long newStreamId = streamManagementService.createStream(streamName);
    return ResponseEntity.ok(new ResourceCreationResponse<>(newStreamId));
  }

  @ResponseBody
  @PutMapping(value = "/streams/{streamName}/video", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResourceCreationResponse<Long>> updateVideo(@PathVariable("streamName") String streamName,
                                                                    @RequestPart("video") MultipartFile video) throws IOException {
    Long newVideoId = playlistManagementService.updateVideo(streamName, video.getInputStream(), video.getOriginalFilename());
    return ResponseEntity.ok(new ResourceCreationResponse<>(newVideoId));
  }

  @ResponseBody
  @PostMapping(value = "/streams/{streamName}/audio", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResourceCreationResponse<Long>> addAudio(@PathVariable("streamName") String streamName,
                                                                 @RequestPart("audio") MultipartFile video) throws IOException {
    Long newAudioId = playlistManagementService.addAudioFile(streamName, video.getInputStream(), video.getOriginalFilename());
    return ResponseEntity.ok(new ResourceCreationResponse<>(newAudioId));
  }

  @ResponseBody
  @DeleteMapping(value = "/audios/{audioId}")
  public ResponseEntity<String> deleteAudio(@PathVariable("audioId") Long audioId) {
    playlistManagementService.deleteAudioFile(audioId);
    return ResponseEntity.ok("OK");
  }

  @ResponseBody
  @GetMapping(value = "/playlists/{streamName}", produces = "application/json")
  public ResponseEntity<PlaylistDto> getPlaylist(@PathVariable("streamName") String streamName) {
    return ResponseEntity.ok(playlistManagementService.getPlaylist(streamName));
  }

  @ResponseBody
  @PutMapping(value = "/playlists/{streamName}/title", produces = "application/json")
  public ResponseEntity<String> updateTitle(@PathVariable("streamName") String streamName,
                                                 @RequestBody StreamTitleDto streamTitleDto) {
    playlistManagementService.updateTitle(streamName, streamTitleDto);
    return ResponseEntity.ok("OK");
  }

}


