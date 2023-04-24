package com.example.examservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "materials")
@Data
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "cluster_id")
    private Long cluterId;

    @Column(name = "exam_id")
    private String examId;
}
