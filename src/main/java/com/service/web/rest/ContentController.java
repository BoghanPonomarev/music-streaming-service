package com.service.web.rest;

import com.service.service.MediaService;
import com.service.service.PlaylistManagementService;
import com.service.stream.context.StreamContext;
import com.service.entity.StreamPortion;
import com.service.service.StreamManagementService;
import com.service.web.builder.ResponseBuilder;
import com.service.web.dto.BaseStreamInfoDto;
import com.service.web.dto.BaseStreamInfoFilterDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ContentController {

  private final MediaService mediaService;
  private final PlaylistManagementService playlistManagementService;
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

  @GetMapping(value = "/streams/{streamName}/pr", produces = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<FileSystemResource> getImage(@PathVariable("streamName") String streamName) {
    File file = new File("src/main/resources/stream-source/"+streamName+"/"+ streamName + "-pr.jpg");
    return ResponseEntity.ok(new FileSystemResource(file));
  }

  @ResponseBody
  @GetMapping(value = "/playlists", produces = "application/json")
  public ResponseEntity<List<BaseStreamInfoDto>> getPlaylistsBaseInfos(BaseStreamInfoFilterDto baseStreamInfoFilterDto) {
    return ResponseEntity.ok(playlistManagementService.getPlaylistsNames(baseStreamInfoFilterDto));
  }

  @ResponseBody
  @GetMapping(value = "/videos/{id}")
  public ResponseEntity<byte[]> getAnimationPart(@PathVariable("id") Long id) throws IOException {
    File animationPart = mediaService.getAnimation(id);
    return ResponseEntity.ok(FileUtils.readFileToByteArray(animationPart));
  }

  @ResponseBody
  @GetMapping(value = "/audios/{id}")
  public ResponseEntity<byte[]> getAudioPart(@PathVariable("id") Long id) throws IOException {
    File animationPart = mediaService.getAudio(id);
    return ResponseEntity.ok(FileUtils.readFileToByteArray(animationPart));
  }

}
