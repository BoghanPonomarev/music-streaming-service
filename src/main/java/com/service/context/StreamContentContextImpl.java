package com.service.context;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.service.parser.StreamUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
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
public class StreamContentContextImpl implements StreamContentContext {

  private long currentStreamIteration = 1;
  private long totalTransportStreamUnitsAmount;
  private boolean isAlive = false;

  @SuppressWarnings("UnstableApiUsage")
  private RangeMap<Long, StreamContentPartition> contentPartitions = TreeRangeMap.create();

  private Lock addLock = new ReentrantLock();

  @Override
  public void startStream() {
    if(!isAlive) {
      contentPartitions.get(currentStreamIteration).startPartitionPlay();
      isAlive = true;
    }
  }

  @Override
  public void addStreamUnits(Queue<StreamUnit> streamUnitQueue) {
    try {
      addLock.lock();
      long streamUnitsSize = putToPartitions(streamUnitQueue);
      totalTransportStreamUnitsAmount += streamUnitsSize;
    } finally {
      addLock.unlock();
    }
  }

  private long putToPartitions(Queue<StreamUnit> streamUnitQueue) {
    long nextStreamUnitId = totalTransportStreamUnitsAmount;
    StreamUnit streamUnit;
    Map<Long, StreamUnit> contentStreamUnits = new ConcurrentHashMap<>();

    while ((streamUnit = streamUnitQueue.poll()) != null) {
      streamUnit.setId(++nextStreamUnitId);
      contentStreamUnits.put(nextStreamUnitId, streamUnit);
    }
    Range<Long> partitionIdsRange = Range.closed(totalTransportStreamUnitsAmount + 1, totalTransportStreamUnitsAmount + contentStreamUnits.size());
    double average = contentStreamUnits.values().stream().mapToDouble(StreamUnit::getDuration).average().orElseThrow(UnsupportedOperationException::new);//TODO change ex to approp
    contentPartitions.put(partitionIdsRange, new StreamContentPartition((long) average, contentStreamUnits));
    return contentStreamUnits.size();
  }

  @Override
  public StreamUnit getStreamUnit(Long unitId) {
    StreamContentPartition requestedContentPartition = contentPartitions.get(unitId);
    return requestedContentPartition != null ? requestedContentPartition.getContentStreamUnit(unitId) : null;
  }

  @Override
  public StreamUnit getCurrentStreamUnit() {
    return getStreamUnit(currentStreamIteration);
  }

  class StreamContentPartition {

    private long partitionCapacity;
    private long iterationDelay;
    private ScheduledExecutorService iterationScheduler;

    private Map<Long, StreamUnit> contentStreamUnits;

    StreamContentPartition(long iterationDelay, Map<Long, StreamUnit> contentStreamUnits) {
      this.iterationDelay = iterationDelay;
      this.contentStreamUnits = contentStreamUnits;
      this.partitionCapacity = contentStreamUnits.size();
    }

    void startPartitionPlay() {
      iterationScheduler = Executors.newScheduledThreadPool(1);
      iterationScheduler.scheduleAtFixedRate(this::iterate, iterationDelay, iterationDelay, TimeUnit.SECONDS);
    }

    private void iterate() {
      partitionCapacity--;
      currentStreamIteration++;
      if(partitionCapacity == 0) {
        stopPartitionPlay();
      }
    }

    void stopPartitionPlay() {
      //TODO clean resources
      StreamContentPartition streamContentPartition = contentPartitions.get(currentStreamIteration);
      streamContentPartition.startPartitionPlay();
      iterationScheduler.shutdownNow();
    }

    StreamUnit getContentStreamUnit(long unitId) {
    return contentStreamUnits.get(unitId);
    }

  }

}
