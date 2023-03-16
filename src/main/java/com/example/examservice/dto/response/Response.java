package com.example.examservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    int code;
    Object body;

    public static Response success(Object body) {
        return new Response(HttpStatus.OK.value(), body);
    }
}
