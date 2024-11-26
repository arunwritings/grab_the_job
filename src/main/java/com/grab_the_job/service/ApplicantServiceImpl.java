package com.grab_the_job.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.grab_the_job.entity.ApplicantEntity;
import com.grab_the_job.pojo.ApplicantIO;
import com.grab_the_job.repository.ApplicantRepo;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ApplicantServiceImpl implements ApplicantService {

    @Autowired
    ApplicantRepo applicantRepo;
    @Autowired
    AmazonS3 amazonS3;
    @Value("${aws.bucketName}")
    String s3BucketName;
    @Override
    public ApplicantEntity createProfile(List<MultipartFile> files, ApplicantIO applicantIO) throws IOException {
        ApplicantEntity applicantEntity;
        applicantEntity = applicantRepo.getApplicantDetails(applicantIO.getEmail());
        if (applicantEntity == null) {
            applicantEntity = new ApplicantEntity();
            applicantEntity.setName(applicantIO.getName());
            applicantEntity.setMobile(applicantIO.getMobile());
            applicantEntity.setEmail(applicantIO.getEmail());
            applicantEntity.setDateOfBirth(applicantIO.getDateOfBirth());
            applicantEntity.setAddress(applicantIO.getAddress());
            applicantEntity.setTenthGradePercentage(applicantIO.getTenthGradePercentage());
            applicantEntity.setTwelfthGradePercentage(applicantIO.getTwelfthGradePercentage());
            applicantEntity.setCollegeName(applicantIO.getCollegeName());
            applicantEntity.setCourseName(applicantIO.getCourseName());
            applicantEntity.setBachelorsCGPA(applicantIO.getBachelorsCGPA());
            List<String> fileUrls = uploadFilesToS3Bucket(files, applicantIO);
            applicantEntity.setFileUrls(fileUrls);
            applicantRepo.save(applicantEntity);
            log.info("ApplicantServiceImpl:: event:Successfully saved the applicant details in Database. method:createProfile name {}, email {}", applicantIO.getName(), applicantIO.getEmail());
        }
        return applicantEntity;
    }

    @Override
    public ApplicantIO getApplicantDetails(String email) {
        ApplicantEntity applicantEntity = applicantRepo.getApplicantDetails(email);
        ApplicantIO applicantIO = null;
        if (applicantEntity != null) {
            applicantIO = new ApplicantIO();
            applicantIO.setName(applicantEntity.getName());
            applicantIO.setEmail(applicantEntity.getEmail());
            applicantIO.setMobile(applicantEntity.getMobile());
            applicantIO.setAddress(applicantEntity.getAddress());
            applicantIO.setDateOfBirth(applicantEntity.getDateOfBirth());
            applicantIO.setCollegeName(applicantEntity.getCollegeName());
            applicantIO.setCourseName(applicantEntity.getCourseName());
            applicantIO.setBachelorsCGPA(applicantEntity.getBachelorsCGPA());
            applicantIO.setTenthGradePercentage(applicantEntity.getTenthGradePercentage());
            applicantIO.setTwelfthGradePercentage(applicantEntity.getTwelfthGradePercentage());
            applicantIO.setFileUrls(applicantEntity.getFileUrls());
        }
        return applicantIO;
    }

    @Override
    public ApplicantEntity updateProfile(List<MultipartFile> files, ApplicantIO applicantIO) throws IOException {
        ApplicantEntity applicantEntity = applicantRepo.getApplicantDetails(applicantIO.getEmail());
        if (applicantEntity == null){
            return createProfile(files,applicantIO);
        }
        // delete old images in s3 bucket
        deleteS3Images(applicantEntity.getFileUrls());
        //upload new images
        List<String> s3ImageUrl = uploadFilesToS3Bucket(files,applicantIO);
        applicantEntity.setFileUrls(s3ImageUrl);
        applicantEntity.setName(applicantIO.getName());
        applicantEntity.setMobile(applicantIO.getMobile());
        applicantEntity.setEmail(applicantIO.getEmail());
        applicantEntity.setDateOfBirth(applicantIO.getDateOfBirth());
        applicantEntity.setAddress(applicantIO.getAddress());
        applicantEntity.setTenthGradePercentage(applicantIO.getTenthGradePercentage());
        applicantEntity.setTwelfthGradePercentage(applicantIO.getTwelfthGradePercentage());
        applicantEntity.setCollegeName(applicantIO.getCollegeName());
        applicantEntity.setCourseName(applicantIO.getCourseName());
        applicantEntity.setBachelorsCGPA(applicantIO.getBachelorsCGPA());
        applicantRepo.save(applicantEntity);
        log.info("ApplicantServiceImpl:: event:Successfully updated the applicant details in Database. method:updateProfile name {}, email {}", applicantIO.getName(), applicantIO.getEmail());
        return applicantEntity;
    }

    @Override
    public void deleteProfile(String email) {
        ApplicantEntity applicantEntity = applicantRepo.getApplicantDetails(email);
        if (applicantEntity != null){
            deleteS3Images(applicantEntity.getFileUrls());
            applicantRepo.delete(applicantEntity);
            log.info("ApplicantServiceImpl:: event:Successfully deleted the applicant details in Database. method:updateProfile, email {}", email);
        }
    }

    private List<String> uploadFilesToS3Bucket(List<MultipartFile> files, ApplicantIO applicantIO) throws IOException {
        List<String> uploadedFileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = generateFileName(file.getOriginalFilename(), applicantIO);
            try {
                InputStream inputStream = file.getInputStream();
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("pdf/jpeg");
                metadata.setContentLength(file.getSize());
                log.info("ApplicantServiceImpl:: event:Before uploading to s3. method:uploadFilesToS3Bucket , email {}, fileSize {}", applicantIO.getEmail(), file.getSize());
                amazonS3.putObject(s3BucketName, fileName, inputStream, metadata);
                log.info("ApplicantServiceImpl:: event:After uploading to s3. method:uploadFilesToS3Bucket , email {}", applicantIO.getEmail());
                String fileUrl =  amazonS3.getUrl(s3BucketName, fileName).toString();
                uploadedFileUrls.add(fileUrl);
            } catch (AmazonS3Exception e) {
                log.error("ApplicantServiceImpl:: exception:AmazonS3Exception, event:Failed to upload to s3. {}", e.getMessage());
            } catch (Exception e) {
                log.error("Error while uploading file to s3. {}", e.getMessage());
                throw e;
            }
        }
        return uploadedFileUrls;
    }

    List<String> extractS3KeyFromUrl(List<String> s3Url) {
        List<String> updated3Keys = new ArrayList<>();
        for (String s3ImageUrl : s3Url){
            String[] parts = s3ImageUrl.split("/");
            String oldKeyName =  parts[parts.length - 1];
            updated3Keys.add(oldKeyName);
        }
       return updated3Keys;
    }
    private String generateFileName(String originalFileName, ApplicantIO applicantIO) {
        return applicantIO.getName() +"_"+ originalFileName;
    }

    private void deleteS3Images(List<String> oldFileUrls){
        if (oldFileUrls != null) {
            for (String oldImageUrl : oldFileUrls){
                String oldKey = extractS3KeyFromUrl(Collections.singletonList(oldImageUrl)).toString();
                try {
                    amazonS3.deleteObject(s3BucketName, oldKey);
                    log.info("ApplicantServiceImpl:: event:Successfully deleted the old files from s3. method:deleteS3Images");
                } catch (AmazonS3Exception e) {
                    log.error("ApplicantServiceImpl:: exception:AmazonS3Exception event:Failed to delete old files from s3. {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Error while deleting old files from s3. e{}", e.getMessage());
                    throw e;
                }
            }
        }
    }

}
