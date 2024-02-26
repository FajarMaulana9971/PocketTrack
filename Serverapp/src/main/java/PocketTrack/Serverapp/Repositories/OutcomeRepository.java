package PocketTrack.Serverapp.Repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface OutcomeRepository extends BaseRepositories<Outcome, String> {
    Optional<Outcome> findByTitle(String title);

    Optional<Outcome> findByDate(LocalDateTime date);

    @Query("SELECT o FROM Outcome o WHERE o.amount = :amount")
    Optional<List<Outcome>> getAllByAmount(BigDecimal amount);

    Optional<List<Outcome>> findByStatusFalse();

    Optional<List<Outcome>> findByStatusTrue();
}
