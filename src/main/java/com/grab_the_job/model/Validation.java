package com.grab_the_job.model;

import com.grab_the_job.entity.ApplicantEntity;
import com.grab_the_job.repository.ApplicantRepo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    @Autowired
    private static ApplicantRepo applicantRepo;
    public static boolean isValid(String email){
        String regex = "^[a-zA-Z0-9+_.-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
