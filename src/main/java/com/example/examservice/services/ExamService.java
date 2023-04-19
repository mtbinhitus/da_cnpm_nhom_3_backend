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

    public Exam saveExam(Exam exam){
        return examRepo.save(exam);
    }

    public Page<Exam> getExams(int page, int limit){
        Pageable sortByDate = PageRequest.of(page, limit, Sort.by("createdDate").descending());
        //Get exam
        return examRepo.findAll(sortByDate);
    }

    public Map<String, Object> getById(Long id){
        Optional<Exam> examOptional = examRepo.findById(id);

        if(examOptional.isEmpty()){
            return null;
        }

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
                    if(option.getQuestionId() == question.getId()){
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
            if(material.getCluterId() == cluster.getId()){
                materials.add(material.getUrl());
            }
        }
        Map<String, Object> clusterPart = new HashMap<>();
        clusterPart.put("questions", questions);
        clusterPart.put("materials", materials);
        partList.add(clusterPart);
    }

}
