package com.service.system.impl;

import com.service.stream.context.StreamPortion;
import com.service.system.SystemResourceCleaner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class DelaySystemResourceCleanerImpl implements SystemResourceCleaner<Collection<StreamPortion>> {

    private static final int DELAY_STREAM_SEGMENTS_TO_REMOVE = 2;

    private SystemResourceCleaner<Collection<StreamPortion>> systemResourceCleaner;
    private Queue<Collection<StreamPortion>> segmentsToRemove;

    public DelaySystemResourceCleanerImpl() {
    systemResourceCleaner = new AsyncSystemResourceCleanerImpl();
    segmentsToRemove = new LinkedList<>();
    }

    @Override
    public void cleanStreamResource(Collection<StreamPortion> resource) {
        segmentsToRemove.add(resource);

        if(segmentsToRemove.size() >= DELAY_STREAM_SEGMENTS_TO_REMOVE) {
            removeNextCollection();
        }
    }

    private void removeNextCollection() {
        Collection<StreamPortion> collectionToRemove = segmentsToRemove.poll();
        if(collectionToRemove != null) {
            systemResourceCleaner.cleanStreamResource(collectionToRemove);
        }
    }

}
