package ma.ensa.mutuelle.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceMedicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @OneToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "pk")
    private Medicine uniqueMedicine;

    @ManyToMany
    @JoinTable(
        name = "reference_medicine_similar",
        joinColumns = @JoinColumn(name = "reference_medicine_id"),
        inverseJoinColumns = @JoinColumn(name = "similar_medicine_id")
    )
    private List<Medicine> similarMedicines;
}
