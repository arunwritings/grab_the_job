package com.grab_the_job.model;

import com.grab_the_job.constants.RespStatus;
import com.grab_the_job.entity.ApplicantEntity;
import com.grab_the_job.repository.ApplicantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Validation {
    private static ApplicantRepo applicantRepo;

    @Autowired
    public Validation(ApplicantRepo applicantRepo) {
        Validation.applicantRepo = applicantRepo;
    }
    public static boolean isValid(String email) {
        String regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
