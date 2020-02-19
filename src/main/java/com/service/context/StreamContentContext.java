package com.service.context;

import com.service.parser.StreamUnit;

import java.util.Queue;

public interface StreamContentContext {

  void startStream();

  void addStreamUnits(Queue<StreamUnit> streamUnitQueue);

  StreamUnit getStreamUnit(Long unitId);

  StreamUnit getCurrentStreamUnit();

}
