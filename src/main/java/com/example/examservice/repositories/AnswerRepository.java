package com.example.examservice.repositories;

import com.example.examservice.entity.Answer;
import com.example.examservice.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Answer findByCorrectNum(int num);
}
