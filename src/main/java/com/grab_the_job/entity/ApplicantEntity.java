package com.grab_the_job.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.File;
import java.util.List;

@Entity
@Data
@Table(name = "grab_the_job")
public class ApplicantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "dateOfBirth")
    private String dateOfBirth;

    @Column(name = "address")
    private String address;

    @Column(name = "tenthGradePercentage")
    private String tenthGradePercentage;

    @Column(name = "twelfthGradePercentage")
    private String twelfthGradePercentage;

    @Column(name = "collegeName")
    private String collegeName;

    @Column(name = "courseName")
    private String courseName;

    @Column(name = "bachelorsCGPA")
    private String bachelorsCGPA;

    @Column(name = "fileUrls")
    private List<String> fileUrls;
}
