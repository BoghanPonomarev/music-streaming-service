package com.service.service.impl;

import com.service.dao.AudioRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.entity.model.Audio;
import com.service.entity.model.Stream;
import com.service.entity.model.Video;
import com.service.entity.enums.StreamStatusConst;
import com.service.exception.EntityNotFoundException;
import com.service.service.PlaylistManagementService;
import com.service.stream.context.StreamContext;
import com.service.stream.holder.StreamContextHolder;
import com.service.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistManagementServiceImpl implements PlaylistManagementService { //TODO Refactoring

  private static final String STREAM_SOURCES_FILE_PATH = "src/main/resources/stream-source";

  private final AudioRepository audioRepository;
  private final StreamRepository streamRepository;
  private final VideoRepository videoRepository;

  @Override
  @Transactional
  public Long updateVideo(String streamName, InputStream videoInputStream, String originalFileName) {
    Stream streamToUpdate = streamRepository.findByName(streamName)
            .orElseThrow(() -> new EntityNotFoundException("No such stream"));
    Long playlistId = streamToUpdate.getPlaylistId();
    deleteCurrentVideos(playlistId);

    String noSpacesOriginalFileName = originalFileName.replace(" ", "");
    File fileToSave = addFile(streamName, videoInputStream, noSpacesOriginalFileName);

    Video newVideo = new Video();
    newVideo.setPlaylistId(playlistId);
    newVideo.setFilePath(fileToSave.getPath());
    Video savedVideo = videoRepository.save(newVideo);
    return savedVideo.getId();
  }

  private void deleteCurrentVideos(Long playlistId) {
    List<Video> videosToDelete = videoRepository.findAllByPlaylistId(playlistId);
    videoRepository.deleteAllByPlaylistId(playlistId);

    for (Video video : videosToDelete) {
      String filePathToDelete = video.getFilePath();
      deleteFile(filePathToDelete);
    }
  }

  @Override
  @Transactional
  public Long addAudioFile(String streamName, InputStream audioInputStream, String originalFileName) {
    Stream streamToAdd = streamRepository.findByName(streamName)
            .orElseThrow(() -> new EntityNotFoundException("No such stream"));
    File fileToSave = addFile(streamName, audioInputStream, originalFileName);

    Audio newAudio = new Audio();
    newAudio.setPlaylistId(streamToAdd.getPlaylistId());
    newAudio.setFilePath(fileToSave.getPath());
    Audio savedAudio = audioRepository.save(newAudio);
    return savedAudio.getId();
  }

  private File addFile(String streamName, InputStream inputStream, String originalFileName) {
    String noSpacesOriginalFileName = originalFileName.replace(" ", "");
    String saveFilePath = STREAM_SOURCES_FILE_PATH + "/" + streamName + "/" + noSpacesOriginalFileName;

    createFileFromStream(saveFilePath, inputStream);

    return new File(saveFilePath);
  }

  @Override
  @Transactional
  public void deleteAudioFile(Long audioId) {
    Audio audioToDelete = audioRepository.findById(audioId)
            .orElseThrow(() -> new EntityNotFoundException("No such file"));

     deleteFile(audioToDelete.getFilePath());
  }

  @Override
  public PlaylistDto getPlaylist(String streamName) {
    Stream stream = streamRepository.findByName(streamName)
            .orElseThrow(() -> new EntityNotFoundException("No such stream"));
    Long playlistId = stream.getPlaylistId();


    String status = StreamStatusConst.valueOf(stream.getStreamStatusId()).getValue();
    return PlaylistDto.builder()
            .playlistId(playlistId)
            .streamId(stream.getId())
            .status(status)
            .streamName(stream.getName())
            .streamTitle(stream.getTitle())
            .streamIteration(getStreamIteration(streamName))
            .audioIdList(audioRepository.findAllByPlaylistId(playlistId).stream().map(this::mapAudio).collect(Collectors.toList()))
            .videoIdList(videoRepository.findAllByPlaylistId(playlistId).stream().map(Video::getId).collect(Collectors.toList()))
            .build();
  }

  private Long getStreamIteration(String streamName) {
    StreamContext streamContext = StreamContextHolder.getStreamContext(streamName);
    if(streamContext != null
            && streamContext.getStreamPortionDto() != null
            && streamContext.getStreamPortionDto().getStreamPortions().get(1) != null) {
      return streamContext.getStreamPortionDto().getStreamPortions().get(1).getId();
    }
    return -1L;
  }

  private MediaDto mapAudio(Audio audio) {
    MediaDto mediaDto = new MediaDto();
    mediaDto.setId(audio.getId());
    String filePath = audio.getFilePath();
    mediaDto.setFileName(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
    return mediaDto;
  }

  @Override
  public List<BaseStreamInfoDto> getPlaylistsNames(BaseStreamInfoFilterDto baseStreamInfoFilterDto) {
    java.util.stream.Stream<BaseStreamInfoDto> baseStream = streamRepository.findAll().stream()
            .map(this::extractStreamHeader);

    List<String> allowedStatuses = baseStreamInfoFilterDto.getAllowedStatuses();
    if(allowedStatuses != null && !allowedStatuses.isEmpty()) {
      return baseStream
              .filter(baseStreamInfoDto -> allowedStatuses.contains(baseStreamInfoDto.getStatus()))
              .collect(Collectors.toList());
    }
    return baseStream
            .collect(Collectors.toList());
  }

  private BaseStreamInfoDto extractStreamHeader(Stream stream) {
    StreamStatusConst streamStatus = StreamStatusConst.valueOf(stream.getStreamStatusId());
    return new BaseStreamInfoDto(stream.getTitle(), stream.getName(), streamStatus.getValue());
  }

  @Override
  @Transactional
  public void updateTitle(String streamName, StreamTitleDto streamTitleDto) {
    Stream stream = streamRepository.findByName(streamName)
            .orElseThrow(() -> new EntityNotFoundException("No such stream"));

    stream.setTitle(streamTitleDto.getStreamTitle());
    streamRepository.save(stream);
  }

  @Override
  public void updatePreview(String streamName, InputStream videoInputStream) {
    String previewFilePath = STREAM_SOURCES_FILE_PATH + "/" + streamName + "/" + streamName + "-pr.jpg";

    deleteFile(previewFilePath);

    createFileFromStream(previewFilePath, videoInputStream);
  }

  @Override
  public void updateCompiledContentFile(String streamName, InputStream videoInputStream) {
    String previewFilePath = STREAM_SOURCES_FILE_PATH + "/" + streamName + "/compiled-content.mp4";

    deleteFile(previewFilePath);

    createFileFromStream(previewFilePath, videoInputStream);
  }

  private void createFileFromStream(String filePath, InputStream inputStream) {
    try {
      File destinationFile = new File(filePath);
      destinationFile.createNewFile();
      FileUtils.copyInputStreamToFile(inputStream, destinationFile);
    } catch (IOException ex) {
      log.error("File creation failed, target stream name - {} ", filePath, ex);
    }
  }

  private void deleteFile(String filePath) {
    try {
      Files.delete(Paths.get(filePath));
    } catch (IOException e) {
      log.error("Failed during file deletion, file path - {}", filePath);
    }
  }

}
