package PocketTrack.Serverapp.Repositories.Base;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.Base.BaseEntity;

@Repository
@Primary
public interface BaseRepositories<E extends BaseEntity, T> extends JpaRepository<E, T>, JpaSpecificationExecutor<E> {

}
