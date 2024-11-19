package ma.ensa.mutuelle.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.ensa.mutuelle.listeners.JobCompletionNotificationListener;
import ma.ensa.mutuelle.models.Folder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
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

    @Bean
    public JsonItemReader<Folder> reader() {
        return new JsonItemReaderBuilder<Folder>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Folder.class))
                .resource(new ClassPathResource("folders.json"))
                .name("folderJsonItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<Folder, Folder> processor() {
        return folder -> {
            // Implement your processing logic here
            return folder;
        };
    }

    @Bean
    public ItemWriter<Folder> writer() {
        return items -> {
            // Implement your writing logic here
        };
    }

    @Bean
    public Job importFolderJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importFolderJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader<Folder> reader, ItemProcessor<Folder, Folder> processor, ItemWriter<Folder> writer) {
        return stepBuilderFactory.get("step1")
                .<Folder, Folder>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}