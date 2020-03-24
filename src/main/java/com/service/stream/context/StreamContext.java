package com.service.stream.context;

import java.util.Queue;

public interface StreamContext {

  void startStream();

  String getStreamName();

  void appendStreamPortions(Queue<StreamPortion> streamPortions);

  StreamPortion getStreamPortion(Long portionId);

  StreamPortionDto getStreamPortionDto();

}
