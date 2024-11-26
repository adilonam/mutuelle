package ma.ensa.mutuelle.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import ma.ensa.mutuelle.models.Dossier;
import ma.ensa.mutuelle.models.MedicamentReferentiel;
import ma.ensa.mutuelle.models.Remboursement;
import ma.ensa.mutuelle.models.Traitement;
import ma.ensa.mutuelle.repositories.MedicamentReferentielRepository;
import ma.ensa.mutuelle.repositories.RemboursementRepository;

@Configuration
public class DossierProcessorConfig {
  @Autowired
    private RemboursementRepository remboursementRepository;

    @Autowired
    private MedicamentReferentielRepository medicamentReferentielRepository;

    private double tauxRemboursement;

    @PostConstruct
    public void init() {
        Remboursement remboursement = remboursementRepository.findByNomRemboursement("consultation");
        if (remboursement != null) {
            tauxRemboursement = remboursement.getTauxRemboursement();
            System.out.println("Remboursement rate for 'consultation': " + tauxRemboursement);
        } else {
            throw new RuntimeException("Remboursement rate for 'consultation' not found.");
        }
    }


    @Bean
    public ItemProcessor<Dossier, Dossier> validationProcessor() {
        
        return dossier -> {
            System.out.println("the validation processor");
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
            System.out.println("Valid dossier: " + dossier.getNomAssure());
            return dossier;
        };
    }

    @Bean
    public ItemProcessor<Dossier, Dossier> consultationProcessor() {
        
   
       
            return dossier -> {
                System.out.println("the Consultation processor");
            // Apply a fixed percentage reimbursement on the consultation price
      
            double montantRembourseConsultation = dossier.getPrixConsultation() * tauxRemboursement; // Example: 70% reimbursement
           
            // dossier.setMontantRemboursementConsultation(consultationReimbursement);
            dossier.setMontantRembourseConsultation(montantRembourseConsultation);
                dossier.setTauxRemboursementConsultation(tauxRemboursement);
            return dossier;
        };
    }

    @Bean
    public ItemProcessor<Dossier, Dossier> traitementMappingProcessor() {
        return dossier -> {
            System.out.println("the Traitement Mapping processor");
            for (Traitement traitement : dossier.getTraitements()) {
                MedicamentReferentiel medicament = medicamentReferentielRepository.findByCodeBarre(traitement.getCodeBarre());
                if (medicament != null) {
                    traitement.setPrixReferentiel(medicament.getPrixMedicament());
                    traitement.setTauxRemboursement(medicament.getTauxRemboursement());
                   
                } else {
                    System.out.println("Medicament not found for code barre: " + traitement.getCodeBarre());
                    traitement.setPrixReferentiel(0.0);
                    traitement.setTauxRemboursement(0.0);
                }
            }
        
            return dossier;
        };
    }

    @Bean
    public ItemProcessor<Dossier, Dossier> traitementRemboursementProcessor() {
        return dossier -> {
            System.out.println("the Traitement Remboursement processor");
            for (Traitement traitement : dossier.getTraitements()) {
                double montantRembourse = traitement.getPrixReferentiel() * traitement.getTauxRemboursement();
                traitement.setMontantRembourse(montantRembourse);
            }
           
            return dossier;
        };
    }

    @Bean
    public ItemProcessor<Dossier, Dossier> totalRemboursementProcessor() {
        return dossier -> {
            System.out.println("the Total Remboursement processor");
            double montantRembourseTraitement = dossier.getTraitements().stream()
                    .mapToDouble(Traitement::getMontantRembourse)
                    .sum();
            double montantTotalRembourse = montantRembourseTraitement + dossier.getMontantRembourseConsultation();
            dossier.setMontantRembourseTraitement(montantRembourseTraitement);
            dossier.setMontantTotalRembourse(montantTotalRembourse);
            System.out.println(" ===================================");
            System.out.println("dossier passed traitementRemboursementProcessor" + dossier);
            System.out.println(" ===================================");
            return dossier;
        };
    }

    @Bean
    public CompositeItemProcessor<Dossier, Dossier> dossierProcessor(ItemProcessor<Dossier, Dossier> validationProcessor,
                                                                     ItemProcessor<Dossier, Dossier> consultationProcessor,
                                                                     ItemProcessor<Dossier, Dossier> traitementMappingProcessor,
                                                                     ItemProcessor<Dossier, Dossier> traitementRemboursementProcessor,
                                                                     ItemProcessor<Dossier, Dossier> totalRemboursementProcessor) {
        List<ItemProcessor<Dossier, Dossier>> processors = Arrays.asList(validationProcessor, consultationProcessor, traitementMappingProcessor, traitementRemboursementProcessor, totalRemboursementProcessor);
        CompositeItemProcessor<Dossier, Dossier> processor = new CompositeItemProcessor<>();
        processor.setDelegates(processors);
        return processor;
    }
}