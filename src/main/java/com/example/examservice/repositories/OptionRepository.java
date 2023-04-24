package com.example.examservice.repositories;

import com.example.examservice.entity.Option;
import com.example.examservice.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByExamId(String examId);
}
