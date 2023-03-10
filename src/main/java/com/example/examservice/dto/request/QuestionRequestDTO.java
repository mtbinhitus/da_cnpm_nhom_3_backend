package com.example.examservice.dto.request;

import com.example.examservice.entity.Option;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequestDTO {
    private String question;
    private List<OptionRequestDTO> options;
    private String explain;
}
