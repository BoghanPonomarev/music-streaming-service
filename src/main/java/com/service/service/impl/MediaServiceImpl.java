package com.service.service.impl;

import com.service.dao.AudioRepository;
import com.service.dao.PlaylistRepository;
import com.service.dao.StreamRepository;
import com.service.dao.VideoRepository;
import com.service.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final AudioRepository audioRepository;
    private final VideoRepository videoRepository;

    @Override
    public File getAnimationPart(Long id) {
        return new File(videoRepository.getOne(id).getFilePath());
    }

    @Override
    public File getAudioPart(Long id) {
        return new File(audioRepository.getOne(id).getFilePath());
    }

}
