package PocketTrack.Serverapp.Repositories;

import org.springframework.stereotype.Repository;

import PocketTrack.Serverapp.Domains.Entities.History;
import PocketTrack.Serverapp.Repositories.Base.BaseRepositories;

@Repository
public interface HistoryRepository extends BaseRepositories<History, String> {

}
