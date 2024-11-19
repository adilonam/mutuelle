package ma.ensa.mutuelle.models;

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
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // private String name;
    // private Double price;

    private String barreCode;
    private Boolean exist;
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "type_medicine_id")
    private TypeMedicine typeMedicine;

    
    private double prix;

}
