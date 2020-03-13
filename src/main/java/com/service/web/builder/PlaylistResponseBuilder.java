package com.service.web.builder;

import com.service.entity.StreamPortion;
import org.springframework.stereotype.Component;

@Component
public class PlaylistResponseBuilder implements ResponseBuilder<String, StreamPortion> {

    @Override
    public String buildResponse(StreamPortion streamPortion) {
        String streamSegmentDecimeter = (streamPortion.isFirstPortionInSegment()) ? "#EXT-X-DISCONTINUITY\n" : "";

        if (streamPortion.getId() < 2) {
            return "#EXTM3U\n" +
                    "#EXT-X-VERSION:3\n" +
                    "#EXT-X-TARGETDURATION:20\n" +
                    "#EXT-X-MEDIA-SEQUENCE:" + streamPortion.getId() + "\n" +
                    "#EXTINF:" + streamPortion.getDuration() + ",\n" +
                    "http://localhost:8080/api/v1/ts/" + streamPortion.getId();
        }

        return "#EXTM3U\n" +
                "#EXT-X-VERSION:3\n" +
                "#EXT-X-TARGETDURATION:20\n" +
                "#EXT-X-MEDIA-SEQUENCE:" + streamPortion.getId() + "\n" +
                "#EXTINF:" + streamPortion.getDuration() + ",\n" +
                "http://localhost:8080/api/v1/streams/" + streamPortion.getStreamName() + "/ts/" + (streamPortion.getId() - 1) + "\n" + streamSegmentDecimeter +
                "#EXTINF:" + streamPortion.getDuration() + ",\n" +
                "http://localhost:8080/api/v1/streams/" + streamPortion.getStreamName() + "/ts/" + streamPortion.getId();
    }

}
