package com.grab_the_job.pojo;

import lombok.Data;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile profilePicture;
    private MultipartFile tenthMarksSheet;
    private MultipartFile twelfthMarksSheet;
    private MultipartFile bachelorsMarksSheet;
    private MultipartFile resume;
    private List<String> fileUrls;
}
