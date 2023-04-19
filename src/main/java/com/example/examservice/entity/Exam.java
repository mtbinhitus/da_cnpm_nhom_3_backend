package com.example.examservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "exams")
@Data
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of exam
     */
    private String name;

    @Column(name = "number_of_takers")
    private Long numTakers;

    @Column(name = "number_of_comments")
    private Long numComments;

    @Column(name = "collection_id")
    private Long collectionId;

    @Column(name = "media_link")
    private String mediaLink;

    @Column(name = "created_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss dd/MM/yyyy")
    private Date createdDate;
}
