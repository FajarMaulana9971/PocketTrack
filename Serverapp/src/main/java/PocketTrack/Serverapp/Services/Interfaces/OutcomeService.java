package PocketTrack.Serverapp.Services.Interfaces;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Models.Requests.OutcomeRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

public interface OutcomeService extends BaseServices<Outcome, String> {
    Outcome getByTitle(String title);

    Outcome getByDate(LocalDateTime date);

    List<Outcome> getByAmount(BigDecimal amount);

    ResponseEntity<ResponseData<Outcome>> getByTitleWithResponse(String title);

    ResponseEntity<ResponseData<Outcome>> getByDateWithResponse(LocalDateTime date);

    ResponseEntity<ResponseData<List<Outcome>>> getByAmountWithResponse(BigDecimal amount);

    ResponseEntity<ResponseData<List<Outcome>>> getByStatusFalseWithResponse();

    ResponseEntity<ResponseData<List<Outcome>>> getByStatusTrueWithResponse();

    ObjectResponseData<Outcome> getAllOutcomeByBudgetId(String budgetId, LocalDateTime date, String title,
            BigDecimal amount, int page, int size);

    ResponseEntity<ResponseData<Outcome>> insertOutcome(OutcomeRequest outcomeRequest);

    ResponseEntity<ResponseData<Outcome>> connectOutcomeWithBUdget(String outcomeId, String budgetId);

    ResponseEntity<ResponseData<Outcome>> deleteOutcome(String id);

}
