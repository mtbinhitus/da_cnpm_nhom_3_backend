package com.example.examservice.controller;

import com.example.examservice.services.StorageFileSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Le Hoang Nhat a.k.a Rei202
 * @Date 2/27/2023
 */

@Controller
@CrossOrigin
@RequestMapping("/material")
//@RolesAllowed("user")
public class MaterialController {
    @Autowired
    StorageFileSevice storageFileSevice;

    @PostMapping("/upload-file")
    ResponseEntity<String> uploadFile(@RequestParam("file")MultipartFile file) throws IOException, InterruptedException {
        String name = "Hello";
        //return resource's url which has just been uploaded
        // constructor contains 2 param. one is file and one is key which stores in s3 as key-value. you can use random string to substitute it instead code below.
        String url = storageFileSevice.saveFile(file, file.getOriginalFilename());
        // store url to db here
        return ResponseEntity.ok(url);
    }
}
