package PocketTrack.Serverapp.Services.Interfaces;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

public interface OutcomeService extends BaseServices<Outcome, String> {
    Outcome getByTitle(String title);

    ResponseEntity<ResponseData<Outcome>> getByTitleWithResponse(String title);
}
