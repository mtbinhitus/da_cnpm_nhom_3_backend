package com.example.examservice.utils;

import com.example.examservice.dto.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {

    public static Integer PART_1 = 1;
    public static Integer PART_2 = 2;
    public static Integer PART_3 = 3;
    public static Integer PART_4 = 4;
    public static Integer PART_5 = 5;
    public static Integer PART_6 = 6;
    public static Integer PART_7 = 7;

    public static ResponseEntity<Response> success (Object body){
        return ResponseEntity.ok(new Response(HttpStatus.OK.value(), body));
    }

    public static ResponseEntity<Response> error (HttpStatus status, Object body){
        return ResponseEntity.ok(new Response(status.value(), body));
    }
}
