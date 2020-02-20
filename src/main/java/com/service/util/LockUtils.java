package com.service.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.locks.Lock;

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

}
