package PocketTrack.Serverapp.Repositories;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface UserRepository extends BaseRepositories<User, String> {

    Optional<User> findByEmail(@Param("email") String email);

    Optional<User> findByNumberPhone(@Param("numberPhone") String numberPhone);

    User findByEmailOrAccount_Username(@Param("email") String email, @Param("username") String username);
}
