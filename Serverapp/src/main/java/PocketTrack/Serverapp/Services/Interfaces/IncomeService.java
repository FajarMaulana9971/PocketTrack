package PocketTrack.Serverapp.Services.Interfaces;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.Income;
import PocketTrack.Serverapp.Domains.Models.Requests.IncomeRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

public interface IncomeService extends BaseServices<Income, String> {

    ResponseEntity<ResponseData<Income>> getByTitleWithResponse(String title);

    ObjectResponseData<Income> getAllIncomeByBudget(String id, String keyword, int page, int size);

    void connectIncomeWithBudget(String incomeId, String BudgetId);

    ResponseEntity<ResponseData<Income>> insertIncome(IncomeRequest incomeRequest);

    ResponseEntity<ResponseData<Income>> addAmountBudgetWithIncomeId(String id, IncomeRequest incomeRequest);
}
