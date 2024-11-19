package ma.ensa.mutuelle.listeners;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Implement logic before job starts
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Implement logic after job completes
    }
}