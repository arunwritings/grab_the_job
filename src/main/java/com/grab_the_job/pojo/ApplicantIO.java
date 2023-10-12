package com.grab_the_job.pojo;

import lombok.Data;

import java.io.File;

@Data
public class ApplicantIO {

    private String name;
    private String mobile;
    private String email;
    private String dateOfBirth;
    private String address;
    private String tenthGradePercentage;
    private String twelfthGradePercentage;
    private String collegeName;
    private String courseName;
    private String bachelorsCGPA;
    private File profilePicture;
    private File tenthMarksSheet;
    private File twelfthMarksSheet;
    private File bachelorsMarksSheet;
    private File resume;
}
