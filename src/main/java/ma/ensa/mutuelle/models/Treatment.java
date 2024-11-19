// File: Traitement.java
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
public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    
    

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

    // Getters and Setters
}