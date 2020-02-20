package com.service.context;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.service.parser.StreamPortion;
import com.service.util.LockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class StreamContextImpl implements StreamContext {

  private static final int DEFAULT_AMONG_ITERATION_DELAY = 10;
  private Lock addLock = new ReentrantLock();
  private Lock startLock = new ReentrantLock();

  private boolean isAlive = false;
  private long currentStreamIteration = 1;
  private long totalStreamPortionsQuantity;

  @SuppressWarnings("UnstableApiUsage")
  private RangeMap<Long, StreamSegment> contentSegments = TreeRangeMap.create();

  @Override
  public void startStream() {
    LockUtils.withLock(startLock, () -> {
      StreamSegment streamSegment = contentSegments.get(currentStreamIteration);
      if (!isAlive && streamSegment != null) {
        streamSegment.startSegmentStream();
        isAlive = true;
      }
    });
  }

  @Override
  public void appendStreamPortions(Queue<StreamPortion> streamPortions) {
    LockUtils.withLock(addLock, () -> {
      StreamSegment newSegment = convertToSegment(streamPortions);

      Range<Long> segmentIdsRange = Range.closed(totalStreamPortionsQuantity + 1, totalStreamPortionsQuantity + newSegment.portionsQuantity);
      contentSegments.put(segmentIdsRange, newSegment);
      totalStreamPortionsQuantity += newSegment.portionsQuantity;
    });
  }

  private StreamSegment convertToSegment(Queue<StreamPortion> streamPortions) {
    Map<Long, StreamPortion> portionsMap = recollectToMap(streamPortions);
            
    double portionAmongIterationDelay = portionsMap.values().stream()
            .mapToDouble(StreamPortion::getDuration)
            .average().orElse(DEFAULT_AMONG_ITERATION_DELAY);
    
    return new StreamSegment((long) portionAmongIterationDelay, portionsMap);
  }

  private Map<Long, StreamPortion> recollectToMap(Queue<StreamPortion> streamPortions) {
    long nextStreamPortionId = totalStreamPortionsQuantity;
    Map<Long, StreamPortion> contentStreamPortions = new ConcurrentHashMap<>();

    StreamPortion streamPortion = streamPortions.poll();
    while (streamPortion != null) {
      streamPortion.setId(++nextStreamPortionId);
      contentStreamPortions.put(nextStreamPortionId, streamPortion);

      streamPortion = streamPortions.poll();
    }

    return contentStreamPortions;
  }

  @Override
  public StreamPortion getStreamPortion(Long portionId) {
    StreamSegment requestedContentSegment = contentSegments.get(portionId);
    return requestedContentSegment != null ? requestedContentSegment.getStreamPortion(portionId) : null;
  }

  @Override
  public StreamPortion getCurrentStreamPortion() {
    return getStreamPortion(currentStreamIteration);
  }

  class StreamSegment {

    private long portionsQuantity;
    private long portionsLeft;
    private long amongIterationDelay;

    private ScheduledExecutorService iterationScheduler;
    private Map<Long, StreamPortion> contentStreamPortions;

    StreamSegment(long amongIterationDelay, Map<Long, StreamPortion> contentStreamPortions) {
      this.amongIterationDelay = amongIterationDelay;
      this.contentStreamPortions = contentStreamPortions;
      this.portionsQuantity = portionsLeft = contentStreamPortions.size();
    }

    void startSegmentStream() {
      iterationScheduler = Executors.newScheduledThreadPool(1);
      iterationScheduler.scheduleAtFixedRate(this::nextPortion, amongIterationDelay, amongIterationDelay, TimeUnit.SECONDS);
    }

    private void nextPortion() {
      portionsLeft--;
      currentStreamIteration++;
      if (portionsLeft == 0) {
        stopSegmentStream();
      }
    }

    void stopSegmentStream() {
      //TODO clean resources
      StreamSegment nextSegment = contentSegments.get(currentStreamIteration);

      if(nextSegment != null) {
        nextSegment.startSegmentStream();
      }
      iterationScheduler.shutdownNow();
    }

    StreamPortion getStreamPortion(long portionId) {
      return contentStreamPortions.get(portionId);
    }

  }

}
