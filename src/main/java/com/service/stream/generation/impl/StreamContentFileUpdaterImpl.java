package com.service.stream.generation.impl;

import com.service.exception.CommandExecutionException;
import com.service.stream.generation.StreamContentFileUpdater;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class StreamContentFileUpdaterImpl implements StreamContentFileUpdater {

    @Override
    public String updateStreamContentFile(String newContentFilePath, String streamName, boolean isStrictUpdate) {
        String contentFilePath = "src/main/resources/stream-source/" + streamName + "/compiled-content.mp4";
        if(!isStrictUpdate) {
            return contentFilePath;
        }

        try {
            File contentFile = new File(contentFilePath);

            if (contentFile.exists()) {
                FileUtils.forceDelete(contentFile);
            }
            FileUtils.moveFile(new File(newContentFilePath), contentFile);
            return contentFilePath;
        } catch (IOException ex) {
            log.error("Failed during content file moving", ex);
            throw new CommandExecutionException("Failed during content file moving", ex);
        }
    }

}
