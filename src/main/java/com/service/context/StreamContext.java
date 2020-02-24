package com.service.context;

import com.service.entity.StreamPortion;

import java.util.Queue;

public interface StreamContext {

  void startStream();

  void appendStreamPortions(Queue<StreamPortion> streamPortions);

  StreamPortion getStreamPortion(Long portionId);

  StreamPortion getCurrentStreamPortion();

}
