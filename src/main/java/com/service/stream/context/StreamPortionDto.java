package com.service.stream.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamPortionDto {

    private List<StreamPortion> streamPortions;

}
