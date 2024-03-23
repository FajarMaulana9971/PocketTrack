package PocketTrack.Serverapp.Repositories;

import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Account;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;
import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface AccountRepository extends BaseRepositories<Account, String> {

    Account findByVerificationCode(@Param("verificationCode") String verificationCode);
}
