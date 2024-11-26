package ma.ensa.mutuelle.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.ensa.mutuelle.models.MedicamentReferentiel;

@Repository
public interface MedicamentReferentielRepository extends JpaRepository<MedicamentReferentiel, Long> {
    MedicamentReferentiel findByCodeBarre(String codeBarre);
}