package com.example.examservice.services;

import com.example.examservice.entity.Exam;
import com.example.examservice.entity.Question;
import com.example.examservice.repositories.ExamRepository;
import com.example.examservice.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepo;

    public Question saveQuestion(Question question){
        return questionRepo.save(question);
    }

}
