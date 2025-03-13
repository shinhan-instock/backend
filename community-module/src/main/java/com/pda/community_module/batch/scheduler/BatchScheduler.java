package com.pda.community_module.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job batchJob;

    @Scheduled(cron = "0 */10 * * * *",zone = "Asia/Seoul")
    public void runBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("targetTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")))
                    .toJobParameters();

            jobLauncher.run(batchJob, jobParameters); //
            System.out.println("배치 실행 완료: " + jobParameters.getString("targetTime"));

        } catch (Exception e) {
            System.err.println("배치 실행 중 오류 발생: " + e.getMessage());
        }
    }
}

