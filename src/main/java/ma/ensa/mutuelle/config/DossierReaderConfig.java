package ma.ensa.mutuelle.config;

import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import ma.ensa.mutuelle.models.Dossier;

@Configuration
public class DossierReaderConfig {

    @Bean
    public JsonItemReader<Dossier> dossierReader() {
        System.out.println("the reader");

        return new JsonItemReaderBuilder<Dossier>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Dossier.class))
                .resource(new ClassPathResource("folders.json"))
                .name("dossierJsonItemReader")
                .build();
    }
}