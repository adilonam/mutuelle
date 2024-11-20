package ma.ensa.mutuelle.repositories;

import ma.ensa.mutuelle.models.MedicamentReferentiel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentReferentielRepository extends JpaRepository<MedicamentReferentiel, Long> {
}