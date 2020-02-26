package com.service.dao;

import com.service.entity.Audio;
import com.service.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioRepository extends JpaRepository<Audio, Long> {

  List<Audio> findAllByPlaylistId(Long playlistId);

}
