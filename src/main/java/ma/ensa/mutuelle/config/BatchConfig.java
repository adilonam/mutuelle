package ma.ensa.mutuelle.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManagerFactory;
import ma.ensa.mutuelle.listeners.JobCompletionNotificationListener;
import ma.ensa.mutuelle.models.Dossier;
import ma.ensa.mutuelle.models.MedicamentReferentiel;
import ma.ensa.mutuelle.repositories.MedicamentReferentielRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MedicamentReferentielRepository medicamentReferentielRepository;

    @Bean
    public JsonItemReader<Dossier> dossierReader() {
        return new JsonItemReaderBuilder<Dossier>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Dossier.class))
                .resource(new ClassPathResource("folders.json"))
                .name("dossierJsonItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<Dossier, Dossier> dossierProcessor() {
        return dossier -> {
            // Validation logic
            if (dossier.getNomAssure() == null || dossier.getNomAssure().isEmpty()) {
                throw new IllegalArgumentException("Nom de l'assuré ne doit pas être vide");
            }
            if (dossier.getNumeroAffiliation() == null || dossier.getNumeroAffiliation().isEmpty()) {
                throw new IllegalArgumentException("Numéro d'affiliation ne doit pas être vide");
            }
            if (dossier.getPrixConsultation() == null || dossier.getPrixConsultation() <= 0) {
                throw new IllegalArgumentException("Prix de la consultation doit être positif");
            }
            if (dossier.getMontantTotalFrais() == null || dossier.getMontantTotalFrais() <= 0) {
                throw new IllegalArgumentException("Montant total des frais doit être positif");
            }
            if (dossier.getTraitements() == null || dossier.getTraitements().isEmpty()) {
                throw new IllegalArgumentException("La liste des traitements doit être présente et non vide");
            }
            return dossier;
        };
    }

   @Bean
public ItemWriter<Dossier> dossierWriter(EntityManagerFactory entityManagerFactory) {
    JpaItemWriter<Dossier> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(entityManagerFactory);
    return writer;
}

    @Bean
    public JsonItemReader<MedicamentReferentiel> medicamentReferentielReader() {
        return new JsonItemReaderBuilder<MedicamentReferentiel>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(MedicamentReferentiel.class))
                .resource(new ClassPathResource("medicaments.json"))
                .name("medicamentReferentielJsonItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<MedicamentReferentiel, MedicamentReferentiel> medicamentReferentielProcessor() {
        return medicamentReferentiel -> {
            // Implement your processing logic here
            return medicamentReferentiel;
        };
    }

    @Bean
    public ItemWriter<MedicamentReferentiel> medicamentReferentielWriter() {
        return new RepositoryItemWriterBuilder<MedicamentReferentiel>()
                .repository(medicamentReferentielRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Job importJob(JobCompletionNotificationListener listener, Step dossierStep, Step medicamentReferentielStep) {
        return jobBuilderFactory.get("importJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(dossierStep)
                .next(medicamentReferentielStep)
                .end()
                .build();
    }

    @Bean
    public Step dossierStep(ItemReader<Dossier> reader, ItemProcessor<Dossier, Dossier> processor, ItemWriter<Dossier> writer) {
        return stepBuilderFactory.get("dossierStep")
                .<Dossier, Dossier>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step medicamentReferentielStep(ItemReader<MedicamentReferentiel> reader, ItemProcessor<MedicamentReferentiel, MedicamentReferentiel> processor, ItemWriter<MedicamentReferentiel> writer) {
        return stepBuilderFactory.get("medicamentReferentielStep")
                .<MedicamentReferentiel, MedicamentReferentiel>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}