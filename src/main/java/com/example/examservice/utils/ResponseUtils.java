package com.example.examservice.utils;

import com.example.examservice.dto.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {
    public static ResponseEntity<Response> success (Object body){
        return ResponseEntity.ok(new Response(HttpStatus.OK.value(), body));
    }

    public static ResponseEntity<Response> error (HttpStatus status, Object body){
        return ResponseEntity.ok(new Response(status.value(), body));
    }
}
