package PocketTrack.Serverapp.Repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface OutcomeRepository extends BaseRepositories<Outcome, String> {
    Optional<Outcome> findByTitle(String title);
}
