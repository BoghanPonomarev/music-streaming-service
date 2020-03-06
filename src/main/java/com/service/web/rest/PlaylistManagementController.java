package com.service.web.rest;

import com.service.service.MediaService;
import com.service.service.PlaylistManagementService;
import com.service.web.dto.PlaylistDto;
import com.service.web.dto.ResourceCreationResponse;
import com.service.web.dto.StreamHeaderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlaylistManagementController {

  private final PlaylistManagementService playlistManagementService;
  private final MediaService mediaService;

  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/playlist/{streamName}", consumes = "application/json", produces = "application/json")
  public ResponseEntity<ResourceCreationResponse<Long>> createPlaylist(@PathVariable("streamName") String streamName) {
    Long newStreamId = playlistManagementService.createStream(streamName);
    return ResponseEntity.ok(new ResourceCreationResponse<>(newStreamId));
  }

  @ResponseBody
  @PutMapping(value = "/playlist/{streamName}/video", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResourceCreationResponse<Long>> updateVideo(@PathVariable("streamName") String streamName,
                                                                    @RequestPart("video") MultipartFile video) throws IOException {
    Long newVideoId = playlistManagementService.updateVideo(streamName, video.getInputStream(), video.getOriginalFilename());
    return ResponseEntity.ok(new ResourceCreationResponse<>(newVideoId));
  }

  @ResponseBody
  @PostMapping(value = "/playlist/{streamName}/audio", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<ResourceCreationResponse<Long>> addAudio(@PathVariable("streamName") String streamName,
                                                                 @RequestPart("audio") MultipartFile video) throws IOException {
    Long newAudioId = playlistManagementService.addAudioFile(streamName, video.getInputStream(), video.getOriginalFilename());
    return ResponseEntity.ok(new ResourceCreationResponse<>(newAudioId));
  }

  @ResponseBody
  @DeleteMapping(value = "/audio/{audioId}", consumes = MULTIPART_FORM_DATA_VALUE, produces = APPLICATION_JSON_VALUE)
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
  @GetMapping(value = "/playlists", produces = "application/json")
  public ResponseEntity<List<StreamHeaderDto>> getPlaylistsNames() {
    return ResponseEntity.ok(playlistManagementService.getPlaylistsNames());
  }

  @ResponseBody
  @GetMapping(value = "/videos/{id}")
  public ResponseEntity<byte[]> getAnimationPart(@PathVariable("id") Long id) throws IOException {
    File animationPart = mediaService.getAnimationPart(id);
    return ResponseEntity.ok(FileUtils.readFileToByteArray(animationPart));
  }

}


