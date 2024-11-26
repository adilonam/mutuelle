package ma.ensa.mutuelle.config;

import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ma.ensa.mutuelle.models.Dossier;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import ma.ensa.mutuelle.repositories.DossierRepository;

@Configuration
public class DossierWriterConfig {

    @Autowired
    private DossierRepository dossierRepository;

    @Bean
    public ItemWriter<Dossier> dossierWriter() {
        return dossiers -> {
         
            System.out.println("the writer");
            for (Dossier dossier : dossiers) {
                dossier.setDateValidationDossier(java.sql.Timestamp.valueOf(LocalDateTime.now()));
                dossierRepository.save(dossier);
                System.out.println(dossier);
            }
        };
    }
}