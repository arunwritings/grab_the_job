package com.grab_the_job.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ApplicantInfoBody {
    private final String message;

    public ApplicantInfoBody(String message) {
        super();
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicantInfoBody that = (ApplicantInfoBody) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }
}
