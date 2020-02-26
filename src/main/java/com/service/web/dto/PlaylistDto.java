package com.service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {

  private Long playlistId;

  private Long streamId;
  private String streamName;
  private String status;
  private List<Long> videoIdList;
  private List<Long> audioIdList;

}
