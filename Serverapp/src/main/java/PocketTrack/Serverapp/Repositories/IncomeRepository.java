package PocketTrack.Serverapp.Repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Income;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface IncomeRepository extends BaseRepositories<Income, String> {
    Optional<Income> findByTitle(String title);

}
