package com.service.web.rest;

import com.service.service.PlaylistManagementService;
import com.service.service.StreamManagementService;
import com.service.web.dto.PlaylistDto;
import com.service.web.dto.ResourceCreationResponse;
import com.service.web.dto.StreamTitleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    public ResponseEntity<ResourceCreationResponse<List<Long>>> addAudio(@PathVariable("streamName") String streamName,
                                                                         @RequestPart("audios") MultipartFile[] audios) throws IOException {
      List<Long> createdNewAudiosIds = new ArrayList<>();

      for (MultipartFile audio : audios) {
        createdNewAudiosIds.add(playlistManagementService.addAudioFile(streamName, audio.getInputStream(),
                audio.getOriginalFilename()));
      }
        return ResponseEntity.ok(new ResourceCreationResponse<>(createdNewAudiosIds));
    }


    @PutMapping(value = "/streams/{streamName}/pr", produces = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updatePreview(@PathVariable("streamName") String streamName,
                                                @RequestPart("preview") MultipartFile previewFile) throws IOException {
        playlistManagementService.updatePreview(streamName, previewFile.getInputStream());
        return ResponseEntity.ok("OK");
    }

    @PutMapping(value = "/streams/{streamName}/contentfile", produces = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> getContentFile(@PathVariable("streamName") String streamName,
                                                 @RequestPart("content") MultipartFile fullContentFile) throws IOException {
        playlistManagementService.updateCompiledContentFile(streamName, fullContentFile.getInputStream());
        return ResponseEntity.ok("OK");
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


