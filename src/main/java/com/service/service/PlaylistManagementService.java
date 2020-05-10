package com.service.service;

import com.service.web.dto.BaseStreamInfoFilterDto;
import com.service.web.dto.PlaylistDto;
import com.service.web.dto.BaseStreamInfoDto;
import com.service.web.dto.StreamTitleDto;

import java.io.InputStream;
import java.util.List;

public interface PlaylistManagementService {

  Long updateVideo(String streamName, InputStream videoInputStream, String originalFileName);

  Long addAudioFile(String streamName, InputStream audioInputStream, String originalFileName);

  void deleteAudioFile(Long audioId);

  PlaylistDto getPlaylist(String streamName);

  List<BaseStreamInfoDto> getPlaylistsNames(BaseStreamInfoFilterDto baseStreamInfoFilterDto);

  void updateTitle(String streamName, StreamTitleDto streamTitleDto);

  void updatePreview(String streamName, InputStream videoInputStream);

  void updateCompiledContentFile(String streamName, InputStream videoInputStream);
}
