package com.service.web.builder;

import com.service.stream.context.StreamPortionDto;
import com.service.stream.context.StreamPortion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PlaylistResponseBuilder implements ResponseBuilder<String, StreamPortionDto> {

    @Value("${server.url}")
    private String serverUrl;

    private static final String GENERAL_PLAYLIST_TAGS = "#EXTM3U\n" +
            "#EXT-X-VERSION:3\n" +
            "#EXT-X-TARGETDURATION:10\n";
    private static final String PLAYLIST_ID_TAG = "#EXT-X-MEDIA-SEQUENCE:";
    private static final String DURATION_TAG = "#EXTINF:";

    @Override
    public String buildResponse(StreamPortionDto streamPortionDto) {
        if (streamPortionDto.getPreviousStreamPortion() == null) {
            return buildStartPlaylistTags(streamPortionDto);
        }

        return buildPlaylistTags(streamPortionDto);
    }

    private String buildStartPlaylistTags(StreamPortionDto streamPortionDto) {
        StreamPortion currentStreamPortion = streamPortionDto.getCurrentStreamPortion();

        return GENERAL_PLAYLIST_TAGS +
                PLAYLIST_ID_TAG + currentStreamPortion.getId() + "\n" +
                DURATION_TAG + currentStreamPortion.getDuration() + ",\n" +
                serverUrl + "/api/v1/streams/" + currentStreamPortion.getStreamName() + "/ts/" + currentStreamPortion.getId();
    }

    private String buildPlaylistTags(StreamPortionDto streamPortionDto) {
        StreamPortion currentStreamPortion = streamPortionDto.getCurrentStreamPortion();
        StreamPortion previousStreamPortion = streamPortionDto.getPreviousStreamPortion();

        String streamName = currentStreamPortion.getStreamName();
        Long currentStreamPortionId = currentStreamPortion.getId();

        return GENERAL_PLAYLIST_TAGS +
                PLAYLIST_ID_TAG + currentStreamPortionId + "\n" +
                DURATION_TAG + previousStreamPortion.getDuration() + ",\n" +
                serverUrl + "/api/v1/streams/" + streamName + "/ts/" + previousStreamPortion.getId() + "\n" +
                getStreamSegmentDelimiter(streamPortionDto) +
                DURATION_TAG + currentStreamPortion.getDuration() + ",\n" +
                serverUrl + "/api/v1/streams/" + streamName + "/ts/" + currentStreamPortionId;
    }

    private String getStreamSegmentDelimiter(StreamPortionDto streamPortionDto) {
        return (streamPortionDto.getCurrentStreamPortion().isFirstPortionInSegment()) ? "#EXT-X-DISCONTINUITY\n" : "";
    }

}
