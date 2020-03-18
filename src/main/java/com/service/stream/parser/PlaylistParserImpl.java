package com.service.stream.parser;

import com.service.entity.StreamPortion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PlaylistParserImpl implements PlaylistParser<String, Queue<StreamPortion>> {

    private static final int REGEXP_DURATION_GROUP_POINTER = 1;
    private static final int REGEXP_FILE_PATH_GROUP_POINTER = 2;

    private Pattern transportStreamChunkPattern = Pattern.compile("#EXTINF:(\\d+\\.?\\d+),(.+?\\.ts)");

    @Override
    public Queue<StreamPortion> parse(String playlistText) {
        Queue<StreamPortion> streamPortionQueue = new LinkedList<>();

        Matcher transportStreamChunkMatcher = transportStreamChunkPattern.matcher(playlistText);
        while (transportStreamChunkMatcher.find()) {
            StreamPortion extractedStreamPortion = extractTransportStreamPortion(transportStreamChunkMatcher);
            streamPortionQueue.add(extractedStreamPortion);
        }

        return streamPortionQueue;
    }

    private StreamPortion extractTransportStreamPortion(Matcher transportStreamChunkMatcher) {
        String duration = transportStreamChunkMatcher.group(REGEXP_DURATION_GROUP_POINTER);
        String filePath = transportStreamChunkMatcher.group(REGEXP_FILE_PATH_GROUP_POINTER);

        StreamPortion resultStreamPortion = new StreamPortion();
        resultStreamPortion.setDuration(Double.valueOf(duration));
        resultStreamPortion.setFilePath(filePath);
        return resultStreamPortion;
    }

}
