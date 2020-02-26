package com.service.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StreamStatusConst {
  CREATED(1, "CREATED"),
  COMPILED(2, "COMPILED"),
  PLAYING(3, "PLAYING"),
  STOPPED(4, "STOPPED");

  private long id;
  private String value;

  public static StreamStatusConst valueOf(long id) {
    for(StreamStatusConst streamStatusConst: values()) {
      if(streamStatusConst.getId() == id) {
        return streamStatusConst;
      }
    }
    return null;
  }

}
