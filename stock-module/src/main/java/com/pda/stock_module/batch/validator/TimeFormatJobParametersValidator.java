package com.pda.stock_module.batch.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;


public class TimeFormatJobParametersValidator implements JobParametersValidator {

    private final String[] requiredKeys;

    public TimeFormatJobParametersValidator(String[] requiredKeys) {
        this.requiredKeys = requiredKeys;
    }

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        for (String key : requiredKeys) {
            String value = parameters.getString(key);
            if (value == null || value.isBlank()) {
                throw new JobParametersInvalidException("Missing required job parameter: " + key);
            }
        }
    }
}
