package com.service;

import lombok.Data;

@Data
public class StreamContentContext {

  private String currentChunkDuration;
  private long currentChunkId;

}
