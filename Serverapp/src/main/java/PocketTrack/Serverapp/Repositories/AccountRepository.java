package PocketTrack.Serverapp.Repositories;

import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface AccountRepository extends BaseRepositories<Account, String> {

}
