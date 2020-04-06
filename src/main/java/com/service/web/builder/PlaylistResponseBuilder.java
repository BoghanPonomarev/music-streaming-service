package com.service.web.builder;

import com.service.stream.context.StreamPortionDto;
import com.service.stream.context.StreamPortion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlaylistResponseBuilder implements ResponseBuilder<String, StreamPortionDto> {

    @Value("${server.url}")
    private String serverUrl;

    private static final String GENERAL_PLAYLIST_TAGS = "#EXTM3U\n" +
            "#EXT-X-VERSION:3\n" +
            "#EXT-X-TARGETDURATION:5\n";
    private static final String PLAYLIST_ID_TAG = "#EXT-X-MEDIA-SEQUENCE:";
    private static final String DURATION_TAG = "#EXTINF:";

    @Override
    public String buildResponse(StreamPortionDto streamPortionDto) {
      /*  if (streamPortionDto.getPreviousStreamPortion() == null) {
            return buildStartPlaylistTags(streamPortionDto);
        }*/

        return buildPlaylistTags(streamPortionDto);
    }

  /*  private String buildStartPlaylistTags(StreamPortionDto streamPortionDto) {
        StreamPortion currentStreamPortion = streamPortionDto.getCurrentStreamPortion();

        return GENERAL_PLAYLIST_TAGS +
                PLAYLIST_ID_TAG + currentStreamPortion.getId() + "\n" +
                DURATION_TAG + currentStreamPortion.getDuration() + ",\n" +
                serverUrl + "/api/v1/streams/" + currentStreamPortion.getStreamName() + "/ts/" + currentStreamPortion.getId();
    }*/

    private String buildPlaylistTags(StreamPortionDto streamPortionDto) {
        List<StreamPortion> streamPortions = streamPortionDto.getStreamPortions();
        StreamPortion currentStreamPortion = streamPortions.get(1);

        String streamName = currentStreamPortion.getStreamName();
        Long currentStreamPortionId = currentStreamPortion.getId();

        StringBuilder  resultPlaylistText = new StringBuilder(GENERAL_PLAYLIST_TAGS +
                PLAYLIST_ID_TAG + currentStreamPortionId + "\n");

        for(int i = 0; i< streamPortions.size() - 1; i++) {
            StreamPortion tmpStreamPortion = streamPortions.get(i);
            resultPlaylistText.append(DURATION_TAG).append(tmpStreamPortion.getDuration())
                    .append(",\n").append(serverUrl).append("/api/v1/streams/").append(streamName)
                    .append("/ts/").append(tmpStreamPortion.getId()).append("\n")
                    .append(getStreamSegmentDelimiter(streamPortions.get(i + 1)));
        }
        StreamPortion lastStreamPortion = streamPortions.get(streamPortions.size() - 1);
        resultPlaylistText.append(DURATION_TAG).append(lastStreamPortion.getDuration())
                .append(",\n").append(serverUrl).append("/api/v1/streams/").append(streamName)
                .append("/ts/").append(lastStreamPortion.getId());

        return resultPlaylistText.toString();
    }

    private String getStreamSegmentDelimiter(StreamPortion streamPortion) {
        return (streamPortion.isFirstPortionInSegment()) ? "#EXT-X-DISCONTINUITY\n" : "";
    }

}
