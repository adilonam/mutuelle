// File: Folder.java
package ma.ensa.mutuelle.models;

import java.util.List;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder {
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

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Treatment> traitements;

    // Getters and Setters
}