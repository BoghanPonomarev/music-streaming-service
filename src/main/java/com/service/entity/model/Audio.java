package com.service.entity.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audio")
public class Audio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "playlist_id")
  private Long playlistId;

  @Column(name = "file_path")
  private String filePath;

}
