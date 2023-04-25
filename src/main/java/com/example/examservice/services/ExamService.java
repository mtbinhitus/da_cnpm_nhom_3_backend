package com.example.examservice.services;

import com.example.examservice.entity.*;
import com.example.examservice.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepo;

    @Autowired
    private OptionRepository optionRepo;

    @Autowired
    private ClusterRepository clusterRepo;

    @Autowired
    private MaterialRepository materialRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private AnswerRepository answerRepo;

    public Exam saveExam(Exam exam){
        return examRepo.save(exam);
    }

    public Page<Exam> getExams(int page, int limit){
        Pageable sortByDate = PageRequest.of(page, limit, Sort.by("createdDate").descending());
        //Get exam
        return examRepo.findAll(sortByDate);
    }

    public Map<String, Object> getById(String id){

        ExecutorService executors = Executors.newFixedThreadPool(5);
        CompletableFuture<List<Option>> optionsFuture = CompletableFuture.supplyAsync(() ->
                        optionRepo.findByExamId(id)
                , executors);
        CompletableFuture<List<Material>> materialsFuture = CompletableFuture.supplyAsync(() ->
                        materialRepo.findByExamId(id)
                , executors);
        CompletableFuture<List<Question>> questionsFuture = CompletableFuture.supplyAsync(() ->
                        questionRepo.findByExamId(id)
                , executors);
        CompletableFuture<List<Cluster>> clustersFuture = CompletableFuture.supplyAsync(() ->
                        clusterRepo.findByExamId(id)
                , executors);
        executors.shutdown();

        List<Cluster> clusterList = clustersFuture.join();
        List<Question> questionList = questionsFuture.join();
        List<Option> optionList = optionsFuture.join();
        List<Material> materialList = materialsFuture.join();


        List<Map<String, Object>> part1List = new ArrayList<>();
        List<Map<String, Object>> part2List = new ArrayList<>();
        List<Map<String, Object>> part3List = new ArrayList<>();
        List<Map<String, Object>> part4List = new ArrayList<>();
        List<Map<String, Object>> part5List = new ArrayList<>();
        List<Map<String, Object>> part6List = new ArrayList<>();
        List<Map<String, Object>> part7List = new ArrayList<>();
        for(Cluster cluster : clusterList){
            switch (cluster.getPart()) {
                case "part1" -> this.addClusterToList(part1List, cluster, questionList, materialList, optionList);
                case "part2" -> this.addClusterToList(part2List, cluster, questionList, materialList, optionList);
                case "part3" -> this.addClusterToList(part3List, cluster, questionList, materialList, optionList);
                case "part4" -> this.addClusterToList(part4List, cluster, questionList, materialList, optionList);
                case "part5" -> this.addClusterToList(part5List, cluster, questionList, materialList, optionList);
                case "part6" -> this.addClusterToList(part6List, cluster, questionList, materialList, optionList);
                case "part7" -> this.addClusterToList(part7List, cluster, questionList, materialList, optionList);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("part1", part1List);
        result.put("part2", part2List);
        result.put("part3", part3List);
        result.put("part4", part4List);
        result.put("part5", part5List);
        result.put("part6", part6List);
        result.put("part7", part7List);

        return result;
    }

    private void addClusterToList(List<Map<String, Object>> partList, Cluster cluster, List<Question> questionList, List<Material> materialList, List<Option> optionList) {
        List<Map<String, Object>> questions = new ArrayList<>();
        List<String> materials = new ArrayList<>();
        for(Question question : questionList){
            if(Objects.equals(question.getCluterId(), cluster.getId())){
                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("id", question.getId());
                questionMap.put("question", question.getQuestion());

                //Create list options
                List<String> options = new ArrayList<>();
                for(Option option : optionList){
                    if(Objects.equals(option.getQuestionId(), question.getId())){
                        options.add(option.getOption());
                    }
                }
                //
                questionMap.put("options", options);
                //
                questions.add(questionMap);
            }
        }
        for(Material material : materialList){
            if(Objects.equals(material.getCluterId(), cluster.getId())){
                materials.add(material.getUrl());
            }
        }
        Map<String, Object> clusterPart = new HashMap<>();
        clusterPart.put("questions", questions);
        clusterPart.put("materials", materials);
        partList.add(clusterPart);
    }

    public Map<String, Object> submitExam(Map<String, Object> map) {
        try{
            // 1. Get list option with examId
            // 2. Loop answer sheet to get correct question
            // 3. Mapping with point table
            // 4. Return listening and writing point
            String examId = map.get("examId").toString();
            List<Map<String, Object>> listening = (List<Map<String, Object>>) map.get("listening");
            List<Map<String, Object>> reading = (List<Map<String, Object>>) map.get("reading");

            List<Option> options = optionRepo.findByExamId(examId);
            int listeningNumber = getCorrectAnswer(options, listening, examId);
            int readingNumber = getCorrectAnswer(options, reading, examId);

            Answer listeningAnswer = answerRepo.findByCorrectNum(listeningNumber);
            Answer readingAnswer = answerRepo.findByCorrectNum(readingNumber);

            Map<String, Object> result = new HashMap<>();
            result.put("listening", listeningAnswer.getListeningPoint());
            result.put("writing", readingAnswer.getWritingPoint());

            return result;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private int getCorrectAnswer(List<Option> options, List<Map<String, Object>> mapList, String id){
        int count = 0;
        for(Map<String, Object> answer : mapList){
            for(Option option : options){
                if(Long.parseLong(answer.get("id").toString()) == option.getQuestionId()
                        && answer.get("answer").toString().equals(option.getOption())){
                    count++;
                }
            }
        }
        return count;
    }
}
