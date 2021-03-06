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
  private String streamTitle;
  private String status;
  private Long streamIteration;
  private List<Long> videoIdList;
  private List<MediaDto> audioIdList;

}
