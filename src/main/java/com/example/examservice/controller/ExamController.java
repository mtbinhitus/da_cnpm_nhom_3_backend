package com.example.examservice.controller;

import com.example.examservice.dto.request.ExamRequestDTO;
import com.example.examservice.dto.response.ExamResponseDTO;
import com.example.examservice.entity.Collection;
import com.example.examservice.entity.Exam;
import com.example.examservice.entity.Option;
import com.example.examservice.entity.Question;
import com.example.examservice.repositories.ExamRepository;
import com.example.examservice.services.CollectionService;
import com.example.examservice.services.ExamService;
import com.example.examservice.services.OptionService;
import com.example.examservice.services.QuestionService;
import com.example.examservice.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping(value = "/exam")
public class ExamController {

    @Autowired
    CollectionService collService;

    @Autowired
    ExamService examService;

    @Autowired
    QuestionService questionService;

    @Autowired
    OptionService optionService;
    //    @Autowired
//    OptionRepository optionRepository;
//
//    @Autowired
//    QuestionRepository questionRepository;
    @Autowired
    ExamRepository examRepository;

    @Autowired
    ModelMapper mapper;

//    @QueryMapping
//    public Iterable<Exam> exams() {
//
//        return examRepository.findAll();
//    }
//
//    @QueryMapping
//    public Iterable<Question> questions(){
//        return questionRepository.findAll();
//    }
//
//    @QueryMapping Iterable<Option> options(){
//        return optionRepository.findAll();
//    }

    /**
     * Create new examination
     *
     * @param body exam request
     * @return JSON result
     */
    @PostMapping("")
    public ResponseEntity<?> createExam(@RequestBody ExamRequestDTO body) {
        try {
            Collection collDb = collService.getCollectionById(body.getCollectionId());
            if (collDb == null) {
                return ResponseUtils.error(HttpStatus.NOT_FOUND, "Not found collection id :: " + body.getCollectionId());
            }

            //Create new exam
            Exam exam = mapper.map(body, Exam.class);
            exam.setNumComments(0L);
            exam.setNumTakers(0L);
            exam.setCreatedDate(new Date());
            exam.setCollectionId(body.getCollectionId());
            exam = examService.saveExam(exam);

            return ResponseUtils.success(exam);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }

    @PostMapping("/questionCrt")
    public ResponseEntity<?> createQuestionInExam(@RequestBody Map<String, Object> map) {
        try {
            //Create option list
            map.put("examId",Long.parseLong( map.get("examId").toString()));
            long startTime = System.nanoTime(); // Lấy thời điểm bắt đầu thực thi hàm
            optionService.createOptionList(map);
            long endTime = System.nanoTime(); // Lấy thời điểm kết thúc thực thi hàm

            long duration = (endTime - startTime) / 1000000; // Tính toán thời gian thực thi hàm

            System.out.println("Thời gian thực thi của hàm là: " + duration + "ms");
            return ResponseUtils.success("Create successfully");
        } catch (Exception e) {
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
//    @PostMapping("/submit")
//    public ResponseEntity<?> submitExam(@RequestBody SubmitRequestDTO map) {
//        try{
//            List<Long> idOptionList = map.getData();
//
//            Iterable<Option> optionList = optionRepository.findAllById(idOptionList);
//
//            int correctNumber = 0;
//            List<Option> optionListResult = new ArrayList<>();
//            for(Option option : optionList){
//                if(option.getIsCorrect()){
//                    optionListResult.add(option);
//                    correctNumber++;
//                }
//            }
//
//            Map<String, Object> result = new LinkedHashMap<>();
//            result.put("correctNum", correctNumber);
//            result.put("point", correctNumber*5);
//            result.put("options", optionListResult);
//
//            return ResponseUtils.success(result);
//        }catch (Exception e){
//            e.printStackTrace();
//            log.error("Exception");
//        }
//        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
//    }

    /**
     * Get detail examination
     *
     * @param id id exam
     * @return JSON result
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getExamDetail(@PathVariable Long id) {
        try {
            Map<String, Object> result = examService.getById(id);

            if (result != null) {
                return ResponseUtils.success(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new exam fail");
    }

    /**
     * Top examination
     *
     * @return JSON result
     */
    @GetMapping("/top")
    public ResponseEntity<?> getExamDetail() {
        try {
            //Get exam
            Pageable sortByDate = PageRequest.of(0, 4, Sort.by("createdDate").descending());
            Page<Exam> examPage = examRepository.findAll(sortByDate);
            List<ExamResponseDTO> examResponseList = new ArrayList<>();
            examPage.getContent().forEach((e) -> examResponseList.add(mapper.map(e, ExamResponseDTO.class)));

            return ResponseUtils.success(examResponseList);
        } catch (Exception e) {
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
