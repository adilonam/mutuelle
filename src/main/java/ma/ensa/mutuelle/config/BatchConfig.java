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
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.List;

@Configuration
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
    public ItemProcessor<Dossier, Dossier> validationProcessor() {
        System.out.println("the processor");
        return dossier -> {
            // Validation logic
            if (dossier.getNomAssure() == null || dossier.getNomAssure().isEmpty()) {
                System.out.println("Invalid dossier: Nom de l'assuré ne doit pas être vide");
                return null;
            }
            if (dossier.getNumeroAffiliation() == null || dossier.getNumeroAffiliation().isEmpty()) {
                System.out.println("Invalid dossier: Numéro d'affiliation ne doit pas être vide");
                return null;
            }
            if (dossier.getPrixConsultation() == null || dossier.getPrixConsultation() <= 0) {
                System.out.println("Invalid dossier: Prix de la consultation doit être positif");
                return null;
            }
            if (dossier.getMontantTotalFrais() == null || dossier.getMontantTotalFrais() <= 0) {
                System.out.println("Invalid dossier: Montant total des frais doit être positif");
                return null;
            }
            if (dossier.getTraitements() == null || dossier.getTraitements().isEmpty()) {
                System.out.println("Invalid dossier: La liste des traitements doit être présente et non vide");
                return null;
            }
            return dossier;
        };
    }

    @Bean
    public ItemProcessor<Dossier, Dossier> calculProcessor() {
        return dossier -> {
            // Apply a fixed percentage reimbursement on the consultation price
            double consultationReimbursement = dossier.getPrixConsultation() * 0.7; // Example: 70% reimbursement
            System.out.println("rembourssement :" + consultationReimbursement);
            // dossier.setMontantRemboursementConsultation(consultationReimbursement);
            return dossier;
        };
    }

    @Bean
    public CompositeItemProcessor<Dossier, Dossier> dossierProcessor(ItemProcessor<Dossier, Dossier> validationProcessor,
                                                                     ItemProcessor<Dossier, Dossier> calculProcessor) {
        List<ItemProcessor<Dossier, Dossier>> processors = Arrays.asList(validationProcessor, calculProcessor);
        CompositeItemProcessor<Dossier, Dossier> processor = new CompositeItemProcessor<>();
        processor.setDelegates(processors);
        return processor;
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
    public Step dossierStep(StepBuilderFactory stepBuilderFactory, PlatformTransactionManager transactionManager, ItemReader<Dossier> reader, @Qualifier("dossierProcessor") ItemProcessor<Dossier, Dossier> processor, ItemWriter<Dossier> writer) {
        return stepBuilderFactory.get("dossierStep5")
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