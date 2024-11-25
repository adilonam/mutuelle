package ma.ensa.mutuelle;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MutuelleApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importJob;

    public static void main(String[] args) {
    
		System.exit(SpringApplication.exit(SpringApplication.run(MutuelleApplication.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        jobLauncher.run(importJob, new JobParametersBuilder()
                .addDate("runDate", new Date())
                .toJobParameters());
    }
}