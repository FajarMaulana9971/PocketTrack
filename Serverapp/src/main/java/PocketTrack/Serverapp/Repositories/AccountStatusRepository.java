package PocketTrack.Serverapp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.AccountStatus;
import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface AccountStatusRepository extends JpaRepository<AccountStatus, Integer> {

    AccountStatus findByName(@Param("status") String status);
}
