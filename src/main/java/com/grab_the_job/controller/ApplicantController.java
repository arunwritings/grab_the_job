package com.grab_the_job.controller;

import com.grab_the_job.constants.RespStatus;
import com.grab_the_job.entity.ApplicantEntity;
import com.grab_the_job.model.ApplicantInfoBody;
import com.grab_the_job.model.Validation;
import com.grab_the_job.pojo.ApplicantIO;
import com.grab_the_job.service.ApplicantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ApplicantController {

    @Autowired
    ApplicantService applicantService;

    @PostMapping("/createProfile")
    ResponseEntity<ApplicantInfoBody> createProfile( @ModelAttribute ApplicantIO applicantIO, @RequestParam("files") List<MultipartFile> files) throws IOException {
        if (applicantIO.getEmail() != null && !applicantIO.getEmail().isEmpty()) {
            if (Validation.isValid(applicantIO.getEmail())) {
                applicantService.createProfile(files,applicantIO);
                return new ResponseEntity<>(new ApplicantInfoBody(RespStatus.SUCCESS), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApplicantInfoBody(RespStatus.INVALID_EMAIL), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApplicantInfoBody(RespStatus.ENTER_EMAIL), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getDetails")
    ResponseEntity<ApplicantIO> getApplicantDetails(@RequestParam("email") String email) {
        return new ResponseEntity<>(applicantService.getApplicantDetails(email),HttpStatus.OK);
    }

    @PutMapping("/updateProfile")
    ResponseEntity<ApplicantEntity> updateProfile(@ModelAttribute ApplicantIO applicantIO, @RequestParam("files") List<MultipartFile> files) throws IOException{
        return new ResponseEntity<>(applicantService.updateProfile(files,applicantIO),HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    ResponseEntity<ApplicantInfoBody> deleteProfile(@RequestParam String email){
        if (email != null && ! email.isEmpty()) {
            if (Validation.isValid(email)) {
                applicantService.deleteProfile(email);
                return new ResponseEntity<>(new ApplicantInfoBody(RespStatus.PROFILE_DELETED), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApplicantInfoBody(RespStatus.INVALID_EMAIL), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ApplicantInfoBody(RespStatus.ENTER_EMAIL), HttpStatus.BAD_REQUEST);
    }
}