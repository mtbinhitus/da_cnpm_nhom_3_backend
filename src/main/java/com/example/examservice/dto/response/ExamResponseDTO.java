package com.example.examservice.dto.response;

import com.example.examservice.dto.request.QuestionRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponseDTO {
    private Long id;
    private String collectionName;
    private String name;
    private List<QuestionResponseDTO> questions;
}
