package com.service.entity;

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
  @Column(name = "playlist_id")
  private Long playlistId;
  @Column(name = "status_id")
  private Long streamStatusId;

}
