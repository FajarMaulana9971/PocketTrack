package PocketTrack.Serverapp.Repositories;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Domains.Entities.AccountRole;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface AccountRoleRepository extends BaseRepositories<AccountRole, String> {
    List<AccountRole> findByAccountId(@Param("accountId") String accountId);

    AccountRole findByAccountAndRoleId(@Param("account") Account account, @Param("roleId") String roleId);
}
