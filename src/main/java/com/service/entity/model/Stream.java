package com.service.entity.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stream")
public class Stream {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;
  @Column
  private String title;
  @Column(name = "playlist_id")
  private Long playlistId;
  @Column(name = "status_id")
  private Long streamStatusId;
  @Column(name ="compilation_iteration")
  private Integer lastCompilationIteration;


}
