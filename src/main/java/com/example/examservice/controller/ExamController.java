package com.example.examservice.controller;

import com.example.examservice.dto.request.ExamRequestDTO;
import com.example.examservice.dto.request.OptionRequestDTO;
import com.example.examservice.dto.request.QuestionRequestDTO;
import com.example.examservice.dto.request.SubmitRequestDTO;
import com.example.examservice.dto.response.ExamResponseDTO;
import com.example.examservice.dto.response.OptionResponseDTO;
import com.example.examservice.dto.response.QuestionResponseDTO;
import com.example.examservice.entity.Collection;
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
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    /**
     * Create new examination
     *
     * @param body exam request
     * @return JSON result
     */
    @PostMapping("")
    public ResponseEntity<?> createExam(@RequestBody ExamRequestDTO body) {
        try{
            Optional<Collection> collectionOptional = collectionRepository.findById(body.getCollectionId());
            if(collectionOptional.isEmpty()){
                return ResponseUtils.error(HttpStatus.NOT_FOUND, "Not found collection id :: " + body.getCollectionId());
            }

            //Create new exam
            Exam exam = mapper.map(body, Exam.class);
            exam.setNumComments(0L);
            exam.setNumTakers(0L);
            exam.setCreatedDate(new Date());
            exam.setCollectionId(body.getCollectionId());
            exam = examRepository.save(exam);

            return ResponseUtils.success(exam);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }

    /**
     * Submit examination to check correct point
     *
     * @param map map
     * @return JSON result
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(@RequestBody SubmitRequestDTO map) {
        try{
            List<Long> idOptionList = map.getData();

            Iterable<Option> optionList = optionRepository.findAllById(idOptionList);

            int correctNumber = 0;
            List<Option> optionListResult = new ArrayList<>();
            for(Option option : optionList){
                if(option.getIsCorrect()){
                    optionListResult.add(option);
                    correctNumber++;
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("correctNum", correctNumber);
            result.put("point", correctNumber*5);
            result.put("options", optionListResult);

            return ResponseUtils.success(result);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }

    /**
     * Get detail examination
     *
     * @param id id exam
     * @return JSON result
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getExamDetail(@PathVariable Long id) {
        try{
            //Get exam
            ExecutorService executors = Executors.newFixedThreadPool(3);
            CompletableFuture<Optional<Exam>> examFuture = CompletableFuture.supplyAsync(() ->
                    examRepository.findById(id), executors);
            executors.shutdown();

            Optional<Exam> examOptional = examFuture.join();

            if(examOptional.isEmpty()){
                return ResponseUtils.error(HttpStatus.NOT_FOUND, "Not found exam with id :: " + id);
            }else {
                Exam exam = examOptional.get();
                return ResponseUtils.success(exam);
            }

        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }

    /**
     * Top examination
     * @return JSON result
     */
    @GetMapping("/top")
    public ResponseEntity<?> getExamDetail() {
        try{
            Pageable sortByDate = PageRequest.of(0, 4, Sort.by("createdDate").descending());
            //Get exam
            Page<Exam> examPage = examRepository.findAll(sortByDate);
            List<ExamResponseDTO> examResponseList = new ArrayList<>();
            examPage.getContent().forEach((e) -> examResponseList.add(mapper.map(e, ExamResponseDTO.class)));

            return ResponseUtils.success(examResponseList);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }

    @GetMapping("/all")
    public ResponseEntity<?> getListExam(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                         @RequestParam(value = "page", defaultValue = "0") Integer page) {
        try{
            Pageable sortByDate = PageRequest.of(page, limit, Sort.by("createdDate").descending());
            //Get exam
            Page<Exam> examPage = examRepository.findAll(sortByDate);
            List<ExamResponseDTO> examResponseList = new ArrayList<>();
            examPage.getContent().forEach((e) -> examResponseList.add(mapper.map(e, ExamResponseDTO.class)));

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("data", examResponseList);
            result.put("total", examPage.getTotalElements());
            result.put("totalPage", examPage.getTotalPages());

            return ResponseUtils.success(result);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }
}
