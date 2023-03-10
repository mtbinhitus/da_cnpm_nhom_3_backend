package com.example.examservice.dto.response;

import com.example.examservice.dto.request.OptionRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDTO {
    private Long id;
    private String question;
    private List<OptionResponseDTO> options;
}
