package PocketTrack.Serverapp.Services.Interfaces;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;

import PocketTrack.Serverapp.Domains.Entities.Budget;
import PocketTrack.Serverapp.Domains.Models.Requests.BudgetRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Services.Interfaces.Base.BaseServices;

public interface BudgetService extends BaseServices<Budget, String> {

    ResponseEntity<ResponseData<Budget>> insertBudget(BudgetRequest budgetRequest);

    ResponseEntity<ResponseData<Budget>> updateBudget(String id, BudgetRequest budgetRequest);

    ResponseEntity<ResponseData<Budget>> getTotalBalanceWithResponse(BigDecimal totalBalance);

    ObjectResponseData<Budget> getAllBudget(String keyword, int page, int size);

}
