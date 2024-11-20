package ma.ensa.mutuelle.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManagerFactory;
import ma.ensa.mutuelle.models.Dossier;
import ma.ensa.mutuelle.repositories.MedicamentReferentielRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public JobBuilderFactory jobBuilderFactory(JobRepository jobRepository) {
        return new JobBuilderFactory(jobRepository);
    }

    @Bean
    public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository) {
        return new StepBuilderFactory(jobRepository);
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public JsonItemReader<Dossier> dossierReader() {
        System.out.println("the reader");
     
        return new JsonItemReaderBuilder<Dossier>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Dossier.class))
                .resource(new ClassPathResource("folders.json"))
                .name("dossierJsonItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<Dossier, Dossier> dossierProcessor() {
        return dossier -> {
            // Print the DTO after reading
            System.out.println("the processor");
            System.out.println(dossier);
            return dossier;
        };
    }

    @Bean
    public ItemWriter<Dossier> dossierWriter() {
        return dossiers -> {
            System.out.println("the writer");
            for (Dossier dossier : dossiers) {
                System.out.println(dossier);
            }
        };
    }

    @Bean
    public Step dossierStep(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager transactionManager, ItemReader<Dossier> reader, ItemProcessor<Dossier, Dossier> processor, ItemWriter<Dossier> writer) {
        return stepBuilderFactory.get("dossierStep")
                .<Dossier, Dossier>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Job importJob(JobBuilderFactory jobBuilderFactory, Step dossierStep) {
        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                .flow(dossierStep)
                .end()
                .build();
    }
}