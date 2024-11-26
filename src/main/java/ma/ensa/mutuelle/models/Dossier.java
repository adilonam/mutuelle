package ma.ensa.mutuelle.models;

import java.util.List;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dossier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomAssure;
    private String numeroAffiliation;
    private String immatriculation;
    private String lienParente;
    private Double montantTotalFrais;
    private Double prixConsultation;
    private Integer nombrePiecesJointes;
    private String nomBeneficiaire;
    private Date dateDepotDossier;
    private Date dateValidationDossier;
    private Double tauxRemboursementConsultation;
    private Double montantRembourseConsultation;
    private Double montantRembourseTraitement;
    private Double montantTotalRembourse;
    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Traitement> traitements;
}