package com.service.dao;

import com.service.entity.model.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AudioRepository extends JpaRepository<Audio, Long> {

  List<Audio> findAllByPlaylistId(Long playlistId);

}
