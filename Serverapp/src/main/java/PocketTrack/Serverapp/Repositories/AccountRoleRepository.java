package PocketTrack.Serverapp.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.AccountRole;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface AccountRoleRepository extends BaseRepositories<AccountRole, String> {
    Optional<List<AccountRole>> findByAccountId(@Param("accountId") String id);
}
