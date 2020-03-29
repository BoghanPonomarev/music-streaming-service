package com.service.stream.context.impl;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.service.stream.content.StreamContentInjector;
import com.service.stream.context.StreamPortionDto;
import com.service.stream.context.StreamContext;
import com.service.stream.context.StreamPortion;
import com.service.system.SystemResourceCleaner;
import com.service.system.impl.DelaySystemResourceCleanerImpl;
import com.service.util.LockUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class StreamContextImpl implements StreamContext {

    private static final int DEFAULT_AMONG_ITERATION_DELAY = 10;
    private Lock appendPortionsLock = new ReentrantLock();
    private Lock startLock = new ReentrantLock();

    private final ExecutorService contentInjectionExecutorService = Executors.newSingleThreadExecutor();
    private final StreamContentInjector streamContentInjector;

    private String streamName;
    private boolean isAlive = false;
    private long currentStreamIteration = 2;
    private long totalStreamPortionsQuantity;

    private RangeMap<Long, StreamSegment> contentSegments = TreeRangeMap.create();
    private final SystemResourceCleaner<Collection<StreamPortion>> systemResourceCleaner;

    public StreamContextImpl(String streamName, StreamContentInjector streamContentInjector) {
        this.streamName = streamName;
        this.streamContentInjector = streamContentInjector;
        this.systemResourceCleaner = new DelaySystemResourceCleanerImpl();
    }

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
        LockUtils.withLock(appendPortionsLock, () -> {
            StreamSegment newSegment = convertToSegment(streamPortions);

            Range<Long> segmentIdsRange = Range.closed(totalStreamPortionsQuantity + 1, totalStreamPortionsQuantity + newSegment.portionsQuantity);
            log.info("Segment with id range from {} to {} was added to stream with name {}",
                    totalStreamPortionsQuantity + 1, totalStreamPortionsQuantity + newSegment.portionsQuantity, streamName);
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

        if (streamPortion != null) streamPortion.setFirstPortionInSegment(true);
        while (streamPortion != null) {
            streamPortion.setId(++nextStreamPortionId);
            streamPortion.setStreamName(streamName);
            contentStreamPortions.put(nextStreamPortionId, streamPortion);

            streamPortion = streamPortions.poll();
        }

        return contentStreamPortions;
    }

    @Override
    public StreamPortion getStreamPortion(Long portionId) {
        StreamSegment requestedContentSegment = contentSegments.get(portionId);
        log.info("Stream portion with id - {} was obtained from stream with name {}, value - {}", portionId, streamName, requestedContentSegment);
        return requestedContentSegment != null ? requestedContentSegment.getStreamPortion(portionId) : null;
    }

    @Override
    public StreamPortionDto getStreamPortionDto() {
        return new StreamPortionDto(getStreamPortion(currentStreamIteration), getStreamPortion(currentStreamIteration - 1));
    }

    @Override
    public String getStreamName() {
        return streamName;
    }

    class StreamSegment {

        private long portionsLeft;
        private long portionsQuantity;
        private long amongIterationDelay;

        private ScheduledExecutorService iterationScheduler;
        private Map<Long, StreamPortion> contentStreamPortions;

        StreamSegment(long amongIterationDelay, Map<Long, StreamPortion> contentStreamPortions) {
            this.amongIterationDelay = amongIterationDelay;
            this.contentStreamPortions = contentStreamPortions;
            this.portionsQuantity = portionsLeft = contentStreamPortions.size();
        }

        void startSegmentStream() {
            contentInjectionExecutorService.execute(() -> streamContentInjector.injectStreamContent(streamName, false));
            iterationScheduler = Executors.newScheduledThreadPool(1);
            iterationScheduler.scheduleAtFixedRate(this::nextPortion, amongIterationDelay, amongIterationDelay, TimeUnit.SECONDS);
        }

        private void nextPortion() {
            portionsLeft--;
            currentStreamIteration++;
            log.info("Current stream iteration - {}, portion to next segment - {} in stream with name {}", currentStreamIteration,
                    portionsLeft, streamName);
            if (portionsLeft == 0) {
                stopSegmentStream();
            }
        }

        void stopSegmentStream() {
            systemResourceCleaner.cleanStreamResource(contentStreamPortions.values());
            startNextSegment();
            iterationScheduler.shutdownNow();
        }

        private void startNextSegment() {
            StreamSegment nextSegment = contentSegments.get(currentStreamIteration);
            log.info("Next stream segment is going to start, stream name - {}, segment value - {}", streamName, nextSegment);
            while (true) {
                if (nextSegment != null) {
                    nextSegment.startSegmentStream();
                    break;
                }
                LockUtils.sleepSecondsLock(5);
            }
        }

        StreamPortion getStreamPortion(long portionId) {
            return contentStreamPortions.get(portionId);
        }

    }

}