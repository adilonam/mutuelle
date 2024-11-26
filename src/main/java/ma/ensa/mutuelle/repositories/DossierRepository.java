
package ma.ensa.mutuelle.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ma.ensa.mutuelle.models.Dossier;

public interface DossierRepository extends JpaRepository<Dossier, Long> {
    // Additional query methods can be defined here if needed
}