package com.service.context;

import com.service.parser.StreamPortion;

import java.util.Queue;

public interface StreamContext {

  void startStream();

  void appendStreamPortions(Queue<StreamPortion> streamPortions);

  StreamPortion getStreamPortion(Long portionId);

  StreamPortion getCurrentStreamPortion();

}
