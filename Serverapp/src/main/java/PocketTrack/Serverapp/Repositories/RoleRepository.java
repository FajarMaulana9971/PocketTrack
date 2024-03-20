package PocketTrack.Serverapp.Repositories;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Role;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface RoleRepository extends BaseRepositories<Role, String> {
    Role findByName(@Param("name") String name);
}
