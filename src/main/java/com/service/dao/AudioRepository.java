package com.service.dao;

import com.service.entity.Audio;
import com.service.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioRepository extends JpaRepository<Audio, Long> {
}
