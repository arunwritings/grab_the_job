package com.grab_the_job.service;

import com.amazonaws.services.s3.AmazonS3;
import com.grab_the_job.entity.ApplicantEntity;
import com.grab_the_job.pojo.ApplicantIO;
import com.grab_the_job.repository.ApplicantRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ApplicantServiceImplTest {

    @Mock
    ApplicantRepo applicantRepo;

    @InjectMocks
    ApplicantServiceImpl applicantServiceImpl;

    @Mock
    private AmazonS3 amazonS3;
    @BeforeEach
    public void setUp() {
        Mockito.reset(applicantRepo);
        ReflectionTestUtils.setField(applicantServiceImpl, "s3BucketName","xyz");
    }
    @Test
    void createProfile() throws IOException {
        ApplicantIO applicantIO = getApplicantIO();
        MultipartFile multipartFile = new MockMultipartFile("files","image1.jpg", "image/jpeg", "your file content".getBytes());
        Mockito.when(applicantRepo.getApplicantDetails(applicantIO.getEmail())).thenReturn(null);
        List<String> s3ImageUrl = Collections.singletonList("https://s3.example.com/"+applicantIO.getName() +"_image1.jpg");
        when(amazonS3.getUrl(anyString(), anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(1);
            return new URL("https://s3.example.com/" + key);
        });
        ApplicantEntity result = applicantServiceImpl.createProfile(Collections.singletonList(multipartFile),applicantIO);
        assertEquals(applicantIO.getEmail(), result.getEmail());
        assertEquals(s3ImageUrl, result.getFileUrls());
        assertEquals(applicantIO.getMobile(), result.getMobile());
        verify(applicantRepo, times(1)).getApplicantDetails(any());
    }

    @Test
    void getApplicantDetails() {
        String email = "xyz@gmail.com";
        when(applicantRepo.getApplicantDetails(any())).thenReturn(getApplicantEntity());
        ApplicantIO result = applicantServiceImpl.getApplicantDetails(email);
        assertEquals("arun",result.getName());
        verify(applicantRepo,times(1)).getApplicantDetails(any());
    }

    @Test
    void updateProfileWhenNotNull() throws IOException {
        ApplicantIO applicantIO = getApplicantIO();
        MultipartFile multipartFile = new MockMultipartFile("files","image1.jpg", "image/jpeg", "your file content".getBytes());
        when(applicantRepo.getApplicantDetails(any())).thenReturn(getApplicantEntity());
        List<String> s3ImageUrl = Collections.singletonList("https://s3.example.com/"+applicantIO.getName() +"_image1.jpg");
        when(amazonS3.getUrl(anyString(), anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(1);
            return new URL("https://s3.example.com/" + key);
        });
        ApplicantEntity result = applicantServiceImpl.updateProfile(Collections.singletonList(multipartFile),applicantIO);
        assertEquals(applicantIO.getEmail(), result.getEmail());
        assertEquals(s3ImageUrl, result.getFileUrls());
        assertEquals(applicantIO.getMobile(), result.getMobile());
        verify(applicantRepo, times(1)).getApplicantDetails(any());
    }

    @Test
    void deleteProfile() {
        String email = "xyz@gmail.com";
        ApplicantEntity applicantEntity = getApplicantEntity();
        when(applicantRepo.getApplicantDetails(any())).thenReturn(getApplicantEntity());
        applicantServiceImpl.deleteProfile(email);
        verify(applicantRepo, times(1)).delete(applicantEntity);
    }

    ApplicantIO getApplicantIO(){
        ApplicantIO applicantIO = new ApplicantIO();
        applicantIO.setName("arun");
        applicantIO.setEmail("xyz@gmail.com");
        applicantIO.setMobile("999");
        return applicantIO;
    }

    ApplicantEntity getApplicantEntity(){
        ApplicantEntity applicantEntity = new ApplicantEntity();
        applicantEntity.setName("arun");
        applicantEntity.setEmail("xyz@gmail.com");
        applicantEntity.setFileUrls(new ArrayList<>());
        applicantEntity.setMobile("999");
        return applicantEntity;
    }
}