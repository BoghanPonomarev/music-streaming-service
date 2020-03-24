package com.service.stream.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamPortionDto {

    private StreamPortion currentStreamPortion;
    private StreamPortion previousStreamPortion;

}
