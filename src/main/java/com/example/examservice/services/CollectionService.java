package com.example.examservice.services;

import com.example.examservice.entity.Collection;
import com.example.examservice.repositories.CollectionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collRepo;

    @Autowired
    private ModelMapper mapper;

    public Collection saveCollection(Collection collection){
        return collRepo.save(collection);
    }

    public List<Collection> getCollectionList(){
        return (List<Collection>) collRepo.findAll();
    }

    public Collection updateCollection(Collection collection, Long id){
        Optional<Collection> collOptional = collRepo.findById(id);

        if(collOptional.isPresent()){
            //Get collection db
            Collection collDb = mapper.map(collection, Collection.class);
            //Save collection update to db
            return collRepo.save(collDb);
        }
        return null;
    }

    public void deleteCollection(Long id){
        Optional<Collection> collOptional = collRepo.findById(id);

        collOptional.ifPresent(collection -> collRepo.delete(collection));
    }

    public Collection getCollectionById(Long id){
        Optional<Collection> collOptional = collRepo.findById(id);

        return collOptional.orElse(null);
    }
}
