package com.grab_the_job.service;

import com.grab_the_job.entity.ApplicantEntity;
import com.grab_the_job.pojo.ApplicantIO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ApplicantService {

    ApplicantEntity createProfile(List<MultipartFile> files,ApplicantIO applicantIO) throws IOException;

    ApplicantIO getApplicantDetails(String email);

    ApplicantEntity updateProfile(List<MultipartFile> files,ApplicantIO applicantIO) throws IOException;

    String deleteProfile(String email);
}
