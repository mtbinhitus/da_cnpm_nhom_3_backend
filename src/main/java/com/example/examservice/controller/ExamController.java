package com.example.examservice.controller;

import com.example.examservice.dto.request.ExamRequestDTO;
import com.example.examservice.dto.request.OptionRequestDTO;
import com.example.examservice.dto.request.QuestionRequestDTO;
import com.example.examservice.dto.response.ExamResponseDTO;
import com.example.examservice.dto.response.OptionResponseDTO;
import com.example.examservice.dto.response.QuestionResponseDTO;
import com.example.examservice.entity.Exam;
import com.example.examservice.entity.Option;
import com.example.examservice.entity.Question;
import com.example.examservice.repositories.CollectionRepository;
import com.example.examservice.repositories.ExamRepository;
import com.example.examservice.repositories.OptionRepository;
import com.example.examservice.repositories.QuestionRepository;
import com.example.examservice.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/exam")
public class ExamController {

    @Autowired
    CollectionRepository collectionRepository;

    @Autowired
    ExamRepository examRepository;

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    ModelMapper mapper;

    @QueryMapping
    public Iterable<Exam> exams() {

        return examRepository.findAll();
    }

    @QueryMapping
    public Iterable<Question> questions(){
        return questionRepository.findAll();
    }

    @QueryMapping Iterable<Option> options(){
        return optionRepository.findAll();
    }

    @PostMapping("")
    public ResponseEntity<?> createExam(@RequestBody ExamRequestDTO body) {
        try{
            //Check collection is exists
            if(!collectionRepository.existsById(body.getCollectionId())){
                return ResponseUtils.error(HttpStatus.NOT_FOUND, "Not found collection id :: " + body.getCollectionId());
            }

            //Create new exam
            Exam exam = mapper.map(body, Exam.class);
            exam.setNumComments(0L);
            exam.setNumTakers(0L);
            exam.setCreatedDate(new Date());
            exam = examRepository.save(exam);

            for (QuestionRequestDTO questionIndex : body.getQuestionList()){
                //Create new question
                Question question = mapper.map(questionIndex, Question.class);
                question.setCreatedDate(new Date());
                question.setExam(exam);
                questionRepository.save(question);
            }


            return ResponseUtils.success(exam);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }

//    @GetMapping("")
//    public ResponseEntity<?> getAllExam() {
//        try{
//            return ResponseUtils.success(examRepository.findAll());
//        }catch (Exception e){
//            e.printStackTrace();
//            log.error("Exception");
//        }
//        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
//    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getExamDetail(@PathVariable Long id) {
        try{
            //Get exam
            Optional<Exam> examOptional = examRepository.findById(id);

            if(examOptional.isEmpty()){
                return ResponseUtils.error(HttpStatus.NOT_FOUND, "Not found exam with id :: " + id);
            }
            Exam exam = examOptional.get();

//            //Get list option and question from list id exam
//            List<Question> questionList = questionRepository.findByExamId(exam.getId());
//            List<Option> optionList = optionRepository.findByExamId(exam.getId());
//
//            //Loop list exam to get data
//            ExamResponseDTO examResponse = mapper.map(exam, ExamResponseDTO.class);
//
//            List<QuestionResponseDTO> questionResponseList = new ArrayList<>();
//            for(Question question : questionList){
//                QuestionResponseDTO questionResponse = new QuestionResponseDTO();
//                if(question.getExamId() == exam.getId()){
//                    questionResponse = mapper.map(question, QuestionResponseDTO.class);
//                }
//
//                List<OptionResponseDTO> optionResponseList = new ArrayList<>();
//                for(Option option : optionList){
//                    OptionResponseDTO optionResponse;
//                    if(option.getQuestionId() == question.getId()){
//                        optionResponse = mapper.map(option, OptionResponseDTO.class);
//                        optionResponseList.add(optionResponse);
//                    }
//                }
//                questionResponse.setOptions(optionResponseList);
//                questionResponseList.add(questionResponse);
//            }
//            examResponse.setQuestions(questionResponseList);

            return ResponseUtils.success(exam);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }
}
