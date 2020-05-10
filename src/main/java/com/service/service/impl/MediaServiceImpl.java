package com.service.service.impl;

import com.service.dao.AudioRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.exception.EntityNotFoundException;
import com.service.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private static final String MEDIA_CONTENT_PATH = "src/main/resources/stream-source/";

    private final AudioRepository audioRepository;
    private final VideoRepository videoRepository;
    private final StreamRepository streamRepository;

    @Override
    public File getAnimation(Long id) {
        return new File(videoRepository.getOne(id).getFilePath());
    }

    @Override
    public File getAudio(Long id) {
        return new File(audioRepository.getOne(id).getFilePath());
    }

}
