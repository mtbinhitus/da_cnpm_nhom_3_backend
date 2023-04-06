package com.example.examservice.dto.response;

import com.example.examservice.dto.request.QuestionRequestDTO;
import com.example.examservice.entity.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponseDTO {
    private Long id;
    private String name;
    private CollectionResponseDTO collection;
    private List<QuestionResponseDTO> questions;
}
