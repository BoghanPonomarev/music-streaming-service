package com.service.service;

import com.service.dao.AudioRepository;
import com.service.dao.PlaylistRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.entity.Audio;
import com.service.entity.Playlist;
import com.service.entity.Stream;
import com.service.entity.Video;
import com.service.entity.enums.StreamStatusConst;
import com.service.exception.EntityNotFoundException;
import com.service.web.dto.PlaylistDto;
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
public class PlaylistManagementServiceImpl implements PlaylistManagementService {

  private static final String STREAM_SOURCES_FILE_PATH = "src/main/resources/stream-source";

  private final AudioRepository audioRepository;
  private final PlaylistRepository playlistRepository;
  private final StreamRepository streamRepository;
  private final VideoRepository videoRepository;

  @Override
  @Transactional
  public Long createStream(String streamName) {
    Playlist newPlaylist = new Playlist();
    playlistRepository.save(newPlaylist);

    Stream newStream = new Stream();
    newStream.setName(streamName);
    newStream.setPlaylistId(newPlaylist.getId());
    newStream.setStreamStatusId(StreamStatusConst.CREATED.getId());

    Stream savedStream = streamRepository.save(newStream);
    createStreamDirectory(streamName);
    return savedStream.getId();
  }

  private void createStreamDirectory(String streamName) {
    try {
      Files.createDirectory(Paths.get(STREAM_SOURCES_FILE_PATH + "/" + streamName));
    } catch (IOException ex) {
      log.error("Stream directory creation failed", ex);
    }
  }

  @Override
  @Transactional
  public Long updateVideo(String streamName, InputStream videoInputStream, String originalFileName) {
    Stream streamToUpdate = streamRepository.findByName(streamName)
            .orElseThrow(() -> new EntityNotFoundException("No such stream"));
    Long playlistId = streamToUpdate.getPlaylistId();
    deleteCurrentVideos(playlistId);

    File fileToSave = addFile(streamName, videoInputStream, originalFileName);

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
      String filePath = video.getFilePath();
      try {
        Files.delete(Paths.get(filePath));
      } catch (IOException e) {
        log.error("Failed during video files deletion, playlist id - {}", playlistId);
      }
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
    String saveFilePath = STREAM_SOURCES_FILE_PATH + "/" + streamName + "/" + originalFileName;

    try {
      File destinationFile = new File(saveFilePath);
      destinationFile.createNewFile();
      FileUtils.copyInputStreamToFile(inputStream, destinationFile);
    } catch (IOException ex) {
      log.error("File creation failed, target stream name - {} ", streamName, ex);
    }

    return new File(saveFilePath);
  }

  @Override
  @Transactional
  public void deleteAudioFile(Long audioId) {
    Audio audioToDelete = audioRepository.findById(audioId)
            .orElseThrow(() -> new EntityNotFoundException("No such file"));
    audioRepository.deleteById(audioId);

    try {
      Files.delete(Paths.get(audioToDelete.getFilePath()));
    } catch (IOException ex) {
      log.error("Failed during audio file deletion, target audio file id - {}", audioId, ex);
    }
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
            .audioIdList(audioRepository.findAllByPlaylistId(playlistId).stream().map(Audio::getId).collect(Collectors.toList()))
            .videoIdList(videoRepository.findAllByPlaylistId(playlistId).stream().map(Video::getId).collect(Collectors.toList()))
            .build();
  }

}