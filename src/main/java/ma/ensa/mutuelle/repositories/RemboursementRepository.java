package ma.ensa.mutuelle.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.ensa.mutuelle.models.Remboursement;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    Remboursement findByNomRemboursement(String nomRemboursement);
}