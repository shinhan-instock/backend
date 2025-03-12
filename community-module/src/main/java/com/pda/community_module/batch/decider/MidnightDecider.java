package com.pda.community_module.batch.decider;

import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class MidnightDecider implements JobExecutionDecider {

    @Override
    public FlowExecutionStatus decide(org.springframework.batch.core.JobExecution jobExecution, org.springframework.batch.core.StepExecution stepExecution) {
        int hour = LocalTime.now(ZoneId.of("Asia/Seoul")).getHour();
        return hour == 0 ? new FlowExecutionStatus("MIDNIGHT") : new FlowExecutionStatus("NOT_MIDNIGHT");
    }
}
