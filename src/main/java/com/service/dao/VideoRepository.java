package com.service.dao;


import com.service.entity.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

  List<Video> findAllByPlaylistId(Long playlistId);

  void deleteAllByPlaylistId(Long playlistId);

}
