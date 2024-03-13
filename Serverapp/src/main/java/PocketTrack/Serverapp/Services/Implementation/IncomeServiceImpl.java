package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.Budget;
import PocketTrack.Serverapp.Domains.Entities.Income;
import PocketTrack.Serverapp.Domains.Models.PageData;
import PocketTrack.Serverapp.Domains.Models.Requests.IncomeRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Repositories.BudgetRepository;
import PocketTrack.Serverapp.Repositories.IncomeRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.BudgetService;
import PocketTrack.Serverapp.Services.Interfaces.IncomeService;
import PocketTrack.Serverapp.Utilities.PaginationUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IncomeServiceImpl extends BaseServicesImpl<Income, String> implements IncomeService {

    private IncomeRepository incomeRepository;
    private BudgetRepository budgetRepository;
    private BudgetService budgetService;
    private PaginationUtil paginationUtil;
    private ModelMapper modelMapper;

    /**
     * This method is used to get income by title
     * 
     * @param title - title of income
     * @return income by title
     */
    public Income getByTitle(String title) {
        return incomeRepository.findByTitle(title).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Title : " + title + " is not found"));
    }

    /**
     * This method is used to get income by title with response
     * 
     * @param title - title of income
     * @return income by title with response
     */
    @Override
    public ResponseEntity<ResponseData<Income>> getByTitleWithResponse(String title) {
        return ResponseEntity.ok(new ResponseData<>(getByTitle(title), "Title : " + title + SUCCESSFULLY_RETRIEVED));
    }

    /**
     * This method is used to connect income with budget use by their id
     * 
     * @param incomeId - id of income
     * @param budgetId - id of budget
     * @return income by income id and budget id connection
     */
    @Override
    public void connectIncomeWithBudget(String incomeId, String budgetId) {
        Income income = getById(incomeId);
        Budget budget = budgetService.getById(budgetId);

        income.setBudget(budget);
        incomeRepository.save(income);
    }

    /**
     * This method is used to get income for user and admin page
     * 
     * @param id      - Id of income
     * @param keyword - Keyword of income
     * @param page    - Page number
     * @param size    - Size per page
     * @return List of income by budgetId with pagination
     */
    @Override
    public ObjectResponseData<Income> getAllIncomeByBudget(String budgetId, String keyword, int page, int size) {
        if (page > 0)
            page = page - 1;

        try {
            Budget budget = budgetService.getById(budgetId);
            List<Income> incomes = budget.getIncome();
            if (keyword != null && !keyword.isEmpty()) {
                incomes = incomes.stream().filter(incoming -> incoming.getTitle().contains(keyword)
                        || incoming.getDate().toString().contains(keyword)).collect(Collectors.toList());
            }
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, incomes.size());
            List<Income> paginatedIncomes = incomes.subList(fromIndex, toIndex);

            Page<Income> pageResult = new PageImpl<>(paginatedIncomes, PageRequest.of(page, size), incomes.size());
            PageData pagination = paginationUtil.setPageData(page, size, incomes.size());
            return new ObjectResponseData<>(pageResult.getContent(), pagination);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to insert new income
     * 
     * @param incomeRequest - Request body of income
     * @return Income with response data
     */
    @Override
    public ResponseEntity<ResponseData<Income>> insertIncome(IncomeRequest incomeRequest) {
        try {
            Income income = modelMapper.map(incomeRequest, Income.class);
            income.setBudget(budgetService.getById(incomeRequest.getBudget()));
            return new ResponseEntity<>(
                    new ResponseData<>(incomeRepository.save(income), "Income" + SUCCESSFULLY_CREATED), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to insert new amount income to budget by incomeId
     * 
     * @param id            - Request of income id
     * @param incomeRequest - Request body of income
     * @return Income by id with response data
     */
    @Override
    public ResponseEntity<ResponseData<Income>> addAmountBudgetWithIncomeId(String id, IncomeRequest incomeRequest) {
        try {
            Income existingIncome = getById(id);
            BigDecimal newAmount = incomeRequest.getAmount();
            existingIncome.setDate(incomeRequest.getDate());
            existingIncome.setDescription(incomeRequest.getDescription());
            existingIncome.setAmount(incomeRequest.getAmount());

            Income updatedIncome = incomeRepository.save(existingIncome);

            Budget budget = updatedIncome.getBudget();
            BigDecimal totalBalance = budget.getTotalBalance().add(newAmount);
            budget.setTotalBalance(totalBalance);
            budgetRepository.save(budget);
            return new ResponseEntity<>(
                    new ResponseData<>(getById(id), "Income" + SUCCESSFULLY_ADDED), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

}
