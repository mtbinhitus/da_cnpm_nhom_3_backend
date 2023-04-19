package com.example.examservice.repositories;

import com.example.examservice.entity.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Long> {
    List<Cluster> findByExamId(Long id);
}
