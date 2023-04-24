package com.example.examservice.services;

import com.example.examservice.entity.*;
import com.example.examservice.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ClusterRepository clusterRepo;

    @Autowired
    private MaterialRepository materialRepo;

    public List<Option> createOptionList(Map<String, Object> map) {
        try {
            List<Option> options = new ArrayList<>();
            List<Material> materials = new ArrayList<>();
            List<String> keyList = Arrays.asList("part1", "part2", "part3", "part4", "part5", "part6", "part7");

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (keyList.contains(entry.getKey())) {
                    Map<String, Object> part = (Map<String, Object>) entry.getValue();
                    List<Map<String, Object>> questionClusters = (List<Map<String, Object>>) part.get("questionClusters");

                    questionClusters.parallelStream().forEach((e) -> {
                        //Create new question cluster
                        Cluster questionCluster = new Cluster();
                        questionCluster.setPart(entry.getKey());
                        questionCluster.setCreatedDate(new Date());
                        questionCluster.setExamId(map.get("examId").toString());

                        CompletableFuture<Cluster> clusterFuture = CompletableFuture.supplyAsync(() ->
                                clusterRepo.save(questionCluster)
                        );
                        CompletableFuture<Void> optionsFuture = clusterFuture.thenAcceptAsync(insertedCluster -> {
                            final long clusterId = insertedCluster.getId();

                            List<Map<String, Object>> questions = (List<Map<String, Object>>) e.get("questions");
                            List<String> materialsMap = (List<String>) e.get("material");

                            this.createMaterialEntity(map.get("examId").toString(), clusterId, materialsMap, materials);
                            this.convertQuestionInput(map.get("examId").toString(), clusterId, questions, options);
                        });
                        futures.add(optionsFuture);
                    });
                }
            }

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));

            try {
                allFutures.join();
                allFutures.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            ExecutorService executors = Executors.newFixedThreadPool(3);
            CompletableFuture<Iterable<Option>> optionsFuture = CompletableFuture.supplyAsync(() ->
                            optionRepo.saveAll(options)
                    , executors);
            CompletableFuture<Void> materialsFuture = CompletableFuture.runAsync(() ->
                            materialRepo.saveAll(materials)
                    , executors);
            executors.shutdown();
            materialsFuture.join();
            List<Option> ops = new ArrayList<>();
            optionsFuture.join().forEach(ops::add);
            return ops;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void createMaterialEntity(String examId, Long clusterId, List<String> materialsMap, List<Material> materials) {
        for(String url : materialsMap){
            Material material = new Material();
            material.setCluterId(clusterId);
            material.setUrl(url);
            material.setExamId(examId);

            materials.add(material);
        }
    }

private void convertQuestionInput(String examId, Long clusterId, List<Map<String, Object>> mapQuestions, List<Option> options) {
    if (mapQuestions.size() > 0) {
        List<CompletableFuture<Question>> questionFutures = new ArrayList<>();
        List<CompletableFuture<Void>> optionFutures = new ArrayList<>();

        for (Map<String, Object> questE : mapQuestions) {
            // Create and save question
            Question question = new Question();
            question.setQuestion(questE.get("questionContent") != null ? questE.get("questionContent").toString() : null);
            question.setExamId(examId);
            question.setCluterId(clusterId);
            question.setCreatedDate(new Date());
            question.setExplain(questE.get("explain").toString());

            // Save question asynchronously
            question = questionRepo.save(question);

            // Create option
            String correctOption = questE.get("correctOption").toString();
            Map<String, String> mapOptions = (Map<String, String>) questE.get("options");

            for (Map.Entry<String, String> entry : mapOptions.entrySet()) {
                Option option = new Option();
                option.setOption(entry.getValue());
                option.setQuestionId(question.getId());
                option.setCreatedDate(new Date());
                option.setIsCorrect(Objects.equals(entry.getKey(), correctOption));
                option.setExamId(examId);

                // Save option asynchronously
                CompletableFuture<Void> optionFuture = CompletableFuture.runAsync(() -> optionRepo.save(option));
                optionFutures.add(optionFuture);
            }
        }

        // Wait for all options to be saved
        CompletableFuture.allOf(optionFutures.toArray(new CompletableFuture[0])).join();
    }
}

}
