package ma.ensa.mutuelle.config;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ma.ensa.mutuelle.models.Dossier;

@Configuration
public class DossierWriterConfig {

    @Bean
    public ItemWriter<Dossier> dossierWriter() {
        return dossiers -> {
            System.out.println("the writer");
            for (Dossier dossier : dossiers) {
                System.out.println(dossier);
            }
        };
    }
}