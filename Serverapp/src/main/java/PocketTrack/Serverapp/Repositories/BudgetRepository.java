package PocketTrack.Serverapp.Repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Budget;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface BudgetRepository extends BaseRepositories<Budget, String> {

    Optional<Budget> findByTotalBalance(BigDecimal totalBalance);
}
