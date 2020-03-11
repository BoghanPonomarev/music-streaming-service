package com.service.service;

import com.service.web.dto.PlaylistDto;
import com.service.web.dto.BaseStreamInfoDto;

import java.io.InputStream;
import java.util.List;

public interface PlaylistManagementService {

  Long updateVideo(String streamName, InputStream videoInputStream, String originalFileName);

  Long addAudioFile(String streamName, InputStream audioInputStream, String originalFileName);

  void deleteAudioFile(Long audioId);

  PlaylistDto getPlaylist(String streamName);

  List<BaseStreamInfoDto> getPlaylistsNames();
}
