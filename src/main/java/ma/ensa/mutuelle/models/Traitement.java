package ma.ensa.mutuelle.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Traitement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codeBarre;
    private Boolean existe;
    private String nomMedicament;
    private String typeMedicament;
    private Double prixMedicament;
    private Double prixReferentiel;
    private Double tauxRemboursement;
    private Double montantRembourse;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private Dossier dossier;
}