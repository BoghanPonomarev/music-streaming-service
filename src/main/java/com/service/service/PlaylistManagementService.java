package com.service.service;

import com.service.web.dto.PlaylistDto;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface PlaylistManagementService {

  Long createStream(String streamName);

  Long updateVideo(String streamName, InputStream videoInputStream, String originalFileName);

  Long addAudioFile(String streamName, InputStream audioInputStream, String originalFileName);

  void deleteAudioFile(Long audioId);

  PlaylistDto getPlaylist(String streamName);

    List<String> getPlaylistsNames();
}
