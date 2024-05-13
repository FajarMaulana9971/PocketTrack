package PocketTrack.Serverapp.Services.Interfaces;

import PocketTrack.Serverapp.Domains.Entities.History;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

public interface HistoryService extends BaseServices<History, String> {

    ObjectResponseData<History> getAllHistoriesBasedOnType(String budgetId, String keywoard, int page, int size);

    ObjectResponseData<History> getAllHistories(String keywoard, int page, int size);

    void deleteHistory();
}
