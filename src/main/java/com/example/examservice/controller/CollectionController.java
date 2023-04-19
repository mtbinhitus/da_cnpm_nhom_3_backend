package com.example.examservice.controller;

import com.example.examservice.dto.request.CollectionRequestDTO;
import com.example.examservice.entity.Collection;
import com.example.examservice.repositories.CollectionRepository;
import com.example.examservice.services.CollectionService;
import com.example.examservice.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(value = "/collection")
public class CollectionController{

    @Autowired
    CollectionService collService;

    @Autowired
    ModelMapper mapper;

    @PostMapping("")
    public ResponseEntity<?> createdCollection(@RequestBody CollectionRequestDTO body) {
        try{
            if (ObjectUtils.isEmpty(body.getName())){
                return ResponseUtils.error(HttpStatus.NO_CONTENT, "No param");
            }
            Collection collection = mapper.map(body, Collection.class);
            collection.setCreatedDate(new Date());
            Collection result = collService.saveCollection(collection);

            return ResponseUtils.success(result);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Created new collection fail");
    }

    @GetMapping()
    public ResponseEntity<?> getAllCollection() {

        Iterable<Collection> collections = collService.getCollectionList();
        return ResponseUtils.success(collections);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCollectionById(@PathVariable Long id){
        try{
            collService.deleteCollection(id);
            return ResponseUtils.success("Delete successfully by id :: " + id);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Delete collection fail");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollection(@PathVariable Long id, @RequestBody Collection collection){
        try{
            Collection updatedColl = collService.updateCollection(collection, id);
            return ResponseUtils.success(updatedColl);
        }catch (Exception e){
            e.printStackTrace();
            log.error("Exception");
        }
        return ResponseUtils.error(HttpStatus.NO_CONTENT, "Delete collection fail");
    }

    @GetMapping("/health-check")
    public ResponseEntity<?> getAPIHealthCheck(){
        return ResponseUtils.success("version: 1.0.0");
    }
}
