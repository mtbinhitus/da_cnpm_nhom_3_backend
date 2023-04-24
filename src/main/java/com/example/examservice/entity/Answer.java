package com.example.examservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "answers")
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correct_num")
    private Integer correctNum;

    @Column(name = "listening_point")
    private Integer listeningPoint;

    @Column(name = "writing_point")
    private Integer writingPoint;
}
