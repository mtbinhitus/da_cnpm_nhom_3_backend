package com.example.examservice.repositories;

import com.example.examservice.entity.Exam;
import com.example.examservice.entity.Option;
import com.example.examservice.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends CrudRepository<Question, Long> {
    List<Question> findAllByExam(Exam exam);
}