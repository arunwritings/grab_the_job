package com.grab_the_job.repository;

import com.grab_the_job.entity.ApplicantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicantRepo extends JpaRepository<ApplicantEntity,Integer> {

    @Query("select m from ApplicantIO m where m.mobile = :mobile")
    ApplicantEntity getApplicantDetails(String mobile);
}
