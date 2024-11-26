package ma.ensa.mutuelle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import ma.ensa.mutuelle.models.Dossier;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.text.SimpleDateFormat;

@Configuration
public class DossierReaderConfig {

    @Bean
    public JsonItemReader<Dossier> dossierReader() {
        System.out.println("the reader");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        JacksonJsonObjectReader<Dossier> jsonObjectReader = new JacksonJsonObjectReader<>(Dossier.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Dossier>()
                .jsonObjectReader(jsonObjectReader)
                .resource(new ClassPathResource("folders.json"))
                .name("dossierJsonItemReader")
                .build();
    }
}