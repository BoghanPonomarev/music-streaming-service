package com.service.dao;

import com.service.entity.Stream;
import com.service.entity.StreamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface StreamRepository extends JpaRepository<Stream, Long> {

  Optional<Stream> findByName(String name);

}
