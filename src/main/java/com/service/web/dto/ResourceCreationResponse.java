package com.service.web.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreationResponse<T> {

  private T newResourceId;

}