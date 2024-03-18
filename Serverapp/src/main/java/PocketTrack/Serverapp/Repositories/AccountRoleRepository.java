package PocketTrack.Serverapp.Repositories;

import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.AccountRole;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface AccountRoleRepository extends BaseRepositories<AccountRole, String> {

}
