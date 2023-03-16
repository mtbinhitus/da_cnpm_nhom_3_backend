package com.example.examservice.repositories;

import com.example.examservice.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends CrudRepository<Exam, Long> {
    Page<Exam>  findAll(Pageable pageable);
}