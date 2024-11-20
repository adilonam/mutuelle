package ma.ensa.mutuelle.models;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private Dossier dossier;
}