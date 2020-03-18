package com.service.web.builder;

import com.service.entity.StreamPortion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PlaylistResponseBuilder implements ResponseBuilder<String, StreamPortion> {

    @Value("${server.url}")
    private String serverUrl;

    private static final String GENERAL_PLAYLIST_TAGS = "#EXTM3U\n" +
            "#EXT-X-VERSION:3\n" +
            "#EXT-X-TARGETDURATION:20\n";
    private static final String PLAYLIST_ID_TAG = "#EXT-X-MEDIA-SEQUENCE:";
    private static final String DURATION_TAG = "#EXTINF:";

    @Override
    public String buildResponse(StreamPortion streamPortion) {
        String streamSegmentDelimiter = (streamPortion.isFirstPortionInSegment()) ? "#EXT-X-DISCONTINUITY\n" : "";

        if (streamPortion.getId() < 2) {
        return buildStartPlaylistTags(streamPortion);
        }

        return buildPlaylistTags(streamPortion, streamSegmentDelimiter);
    }

    private String buildStartPlaylistTags(StreamPortion streamPortion) {
        return GENERAL_PLAYLIST_TAGS +
                PLAYLIST_ID_TAG + streamPortion.getId() + "\n" +
                DURATION_TAG + streamPortion.getDuration() + ",\n" +
                serverUrl + "/api/v1/ts/" + streamPortion.getId();
    }

    private String buildPlaylistTags(StreamPortion streamPortion, String streamSegmentDelimiter) {
        return GENERAL_PLAYLIST_TAGS +
                PLAYLIST_ID_TAG + streamPortion.getId() + "\n" +
                DURATION_TAG + streamPortion.getDuration() + ",\n" +
                serverUrl + "/api/v1/streams/" + streamPortion.getStreamName() + "/ts/" + (streamPortion.getId() - 1) + "\n" +
                streamSegmentDelimiter +
                DURATION_TAG + streamPortion.getDuration() + ",\n" +
                serverUrl + "/api/v1/streams/" + streamPortion.getStreamName() + "/ts/" + streamPortion.getId();
    }

}
