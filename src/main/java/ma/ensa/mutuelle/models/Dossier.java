package ma.ensa.mutuelle.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String dateDepotDossier;

    @OneToMany(mappedBy = "dossier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Traitement> traitements;
}