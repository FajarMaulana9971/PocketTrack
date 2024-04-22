package PocketTrack.Serverapp.Services.Implementation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.*;

import PocketTrack.Serverapp.Domains.Entities.Budget;
import PocketTrack.Serverapp.Domains.Entities.History;
import PocketTrack.Serverapp.Domains.Entities.Income;
import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Entities.User;
import PocketTrack.Serverapp.Domains.Models.PageData;
import PocketTrack.Serverapp.Domains.Models.Requests.BudgetRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Repositories.BudgetRepository;
import PocketTrack.Serverapp.Repositories.HistoryRepository;
import PocketTrack.Serverapp.Repositories.IncomeRepository;
import PocketTrack.Serverapp.Repositories.OutcomeRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.BudgetService;
import PocketTrack.Serverapp.Utilities.GenericSpecificationsBuilder;
import PocketTrack.Serverapp.Utilities.PaginationUtil;
import PocketTrack.Serverapp.Utilities.SpecificationFactory;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BudgetServiceImpl extends BaseServicesImpl<Budget, String> implements BudgetService {

    private BudgetRepository budgetRepository;
    private ModelMapper modelMapper;
    private SpecificationFactory<Budget> specificationFactory;
    private PaginationUtil paginationUtil;
    private IncomeRepository incomeRepository;
    private OutcomeRepository outcomeRepository;
    private HistoryRepository historyRepository;

    /**
     * This method is used to get budget by total balance
     * 
     * @param totalBalance - totalBalance of budget
     * @return budget by total balance
     */
    public Budget getByTotalBalance(BigDecimal totalBalance) {
        return budgetRepository.findByTotalBalance(totalBalance)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Total balance " + totalBalance + "is not found"));
    }

    /**
     * This method is used to get budget response by total balance
     * 
     * @param totalBalance - totalBalance of budget
     * @return budget with response data
     */
    @Override
    public ResponseEntity<ResponseData<Budget>> getTotalBalanceWithResponse(BigDecimal totalBalance) {
        return ResponseEntity.ok(new ResponseData<>(getByTotalBalance(totalBalance),
                "Total balance :" + totalBalance + " " + SUCCESSFULLY_RETRIEVED));
    }

    /**
     * This method is used to get budget for admin page
     * 
     * @param keyword - Keyword for search
     * @param page    - Page number
     * @param size    - Size per page
     * @return List of budget with pagination
     */
    @Override
    public ObjectResponseData<Budget> getAllBudget(String keyword, int page, int size) {
        if (page > 0)
            page = page - 1;

        GenericSpecificationsBuilder<Budget> budgetBuilder = new GenericSpecificationsBuilder<>();
        if (keyword != null) {
            budgetBuilder.with(specificationFactory.isContain("totalBalance", keyword)
                    .or(specificationFactory.isContain("date", keyword)));
        }
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Budget> budgetPage = budgetRepository.findAll(budgetBuilder.build(), pageable);
            PageData pagination = paginationUtil.setPageData(page, size, (int) budgetPage.getTotalElements());
            return new ObjectResponseData<>(budgetPage.getContent(), pagination);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to insert new budget
     * 
     * @param budgetRequest - Request body of budget
     * @return budget with response data
     */
    @Override
    public ResponseEntity<ResponseData<Budget>> insertBudget(BudgetRequest budgetRequest) {
        try {
            Budget budget = modelMapper.map(budgetRequest, Budget.class);

            ZoneId zoneId = ZoneId.of("Asia/Jakarta");
            LocalDateTime now = LocalDateTime.now(zoneId);
            budget.setDate(now);
            Budget savedBudget = budgetRepository.save(budget);

            Income income = new Income();
            income.setDate(now);
            income.setDescription("First commit by budget create".toLowerCase());
            income.setTitle("First Commit".toUpperCase());
            income.setAmount(BigDecimal.ZERO);
            income.setBudget(savedBudget);
            incomeRepository.save(income);

            Outcome outcome = new Outcome();
            outcome.setDate(now);
            outcome.setDescription("first commit by budget create".toLowerCase());
            outcome.setTitle("first commit".toUpperCase());
            outcome.setAmount(BigDecimal.ZERO);
            outcome.setBudget(savedBudget);
            outcome.setIsDeleted(false);
            outcome.setStatus(true);
            outcomeRepository.save(outcome);

            History history = new History();
            history.setDate(now);
            history.setNotes(budgetRequest.getDescription());
            history.setBudget(savedBudget);
            historyRepository.save(history);

            return new ResponseEntity<>(
                    new ResponseData<>(budgetRepository.save(savedBudget), "Budget" + SUCCESSFULLY_CREATED),
                    HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to update budget
     * 
     * @param id            - ID of budget
     * @param budgetRequest - Request body of budget
     * @return budget with response data
     */
    @Override
    public ResponseEntity<ResponseData<Budget>> updateBudget(String id, BudgetRequest budgetRequest) {
        try {
            Budget budget = modelMapper.map(budgetRequest, Budget.class);
            ZoneId zoneId = ZoneId.of("Asia/Jakarta");
            LocalDateTime now = LocalDateTime.now(zoneId);

            budget.setTotalBalance(budgetRequest.getTotalBalance());
            budget.setId(getById(id).getId());
            budget.setDate(now);
            return new ResponseEntity<>(
                    new ResponseData<>(budgetRepository.save(budget), "Budget" + SUCCESSFULLY_UPDATED), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }
}
