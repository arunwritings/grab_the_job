package com.grab_the_job.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.grab_the_job.entity.ApplicantEntity;
import com.grab_the_job.pojo.ApplicantIO;
import com.grab_the_job.repository.ApplicantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ApplicantServiceImpl implements ApplicantService {

    @Autowired
    ApplicantRepo applicantRepo;

    @Autowired
    AmazonS3 amazonS3;

    @Value("${aws.bucketName}")
    String s3BucketName;
    @Override
    public ApplicantEntity createProfile(List<MultipartFile> files, ApplicantIO applicantIO) throws IOException {
        ApplicantEntity applicantEntity = new ApplicantEntity();
        applicantEntity.setName(applicantIO.getName());
        applicantEntity.setMobile(applicantIO.getMobile());
        applicantEntity.setEmail(applicantIO.getEmail());
        applicantEntity.setDateOfBirth(applicantIO.getDateOfBirth());
        applicantEntity.setAddress(applicantIO.getAddress());
        applicantEntity.setTenthGradePercentage(applicantIO.getTenthGradePercentage());
        applicantEntity.setTwelfthGradePercentage(applicantEntity.getTwelfthGradePercentage());
        applicantEntity.setCollegeName(applicantIO.getCollegeName());
        applicantEntity.setCourseName(applicantIO.getCourseName());
        applicantEntity.setBachelorsCGPA(applicantIO.getBachelorsCGPA());
        for (String urls : uploadFilesToS3Bucket(files,applicantIO)){
            applicantEntity.setFileUrls(Collections.singletonList(urls));
        }
        applicantRepo.save(applicantEntity);
        return applicantEntity;
    }

    private List<String> uploadFilesToS3Bucket(List<MultipartFile> files, ApplicantIO applicantIO) throws IOException {
        List<String> uploadedFileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = generateFileName(file.getOriginalFilename());
            try {
                InputStream inputStream = file.getInputStream();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("pdf/jpeg");
                metadata.setContentLength(file.getSize());
                amazonS3.putObject(s3BucketName, fileName, inputStream, metadata);
                String fileUrl =  amazonS3.getUrl(s3BucketName, fileName).toString();
                uploadedFileUrls.add(fileUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return uploadedFileUrls;
    }

    private String generateFileName(String originalFileName) {
        return System.currentTimeMillis() + "_" + originalFileName;
    }
}