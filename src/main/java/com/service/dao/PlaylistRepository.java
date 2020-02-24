package com.service.dao;

import com.service.entity.Playlist;
import com.service.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

}
