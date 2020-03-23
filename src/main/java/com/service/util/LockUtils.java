package com.service.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;

@Slf4j
@UtilityClass
public class LockUtils {

  public void withLock(Lock lock, Runnable function) {
    try {
      lock.lock();
      function.run();
    } finally {
      lock.unlock();
    }
  }

  public void sleepSecondsLock(int sleepSeconds) {
    try {
      Thread.sleep(sleepSeconds * 1000);
    } catch (InterruptedException ex) {
      log.error("Sleep was interrupted", ex);
    }
  }

}
