package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import PocketTrack.Serverapp.Domains.Entities.History;
import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Enums.Type;
import PocketTrack.Serverapp.Domains.Models.PageData;
import PocketTrack.Serverapp.Domains.Models.Requests.OutcomeRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Repositories.BudgetRepository;
import PocketTrack.Serverapp.Repositories.HistoryRepository;
import PocketTrack.Serverapp.Repositories.OutcomeRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.BudgetService;
import PocketTrack.Serverapp.Services.Interfaces.OutcomeService;
import PocketTrack.Serverapp.Utilities.PaginationUtil;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OutcomeServiceImpl extends BaseServicesImpl<Outcome, String> implements OutcomeService {

    private OutcomeRepository outcomeRepository;
    private PaginationUtil paginationUtil;
    private ModelMapper modelMapper;
    private BudgetService budgetService;
    private BudgetRepository budgetRepository;
    private HistoryRepository historyRepository;

    /**
     * This method is used to get outcome by title
     * 
     * @param title - title of outcome
     * @return outcome by title
     */
    @Override
    public Outcome getByTitle(String title) {
        return outcomeRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "title : " + title + NOT_FOUND));
    }

    /**
     * This method is used to get outcome by title with response
     * 
     * @param title - title of outcome
     * @return outcome by title with response
     */
    @Override
    public ResponseEntity<ResponseData<Outcome>> getByTitleWithResponse(String title) {
        return ResponseEntity.ok(new ResponseData<>(getByTitle(title), "Outcome" + SUCCESSFULLY_RETRIEVED));
    }

    /**
     * This method is used to get outcome by date
     * 
     * @param date - date of outcome
     * @return outcome by date
     */
    @Override
    public Outcome getByDate(LocalDateTime date) {
        return outcomeRepository.findByDate(date)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Date : " + date + NOT_FOUND));
    }

    /**
     * This method is used to get outcome by date with response
     * 
     * @param date - date of outcome
     * @return outcome by date with response
     */
    @Override
    public ResponseEntity<ResponseData<Outcome>> getByDateWithResponse(LocalDateTime date) {
        return ResponseEntity.ok(new ResponseData<>(getByDate(date), "Date" + SUCCESSFULLY_RETRIEVED));
    }

    /**
     * This method is used to get outcome by amount
     * 
     * @param amount - amount of outcome
     * @return outcome by amount
     */
    @Override
    public List<Outcome> getByAmount(BigDecimal amount) {
        return outcomeRepository.getAllByAmount(amount).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Income amount : " + amount + NOT_FOUND));
    }

    /**
     * This method is used to get outcome by amount with response
     * 
     * @param amount - amount of outcome
     * @return outcome by amount with response
     */
    @Override
    public ResponseEntity<ResponseData<List<Outcome>>> getByAmountWithResponse(BigDecimal amount) {
        return ResponseEntity
                .ok(new ResponseData<>(getByAmount(amount), "Outcome amount : " + amount + SUCCESSFULLY_RETRIEVED));
    }

    /**
     * This method is used to get outcome by status true value with response
     * 
     * @param status - status of outcome
     * @return outcome by status true with response
     */
    @Override
    public ResponseEntity<ResponseData<List<Outcome>>> getByStatusFalseWithResponse() {
        return ResponseEntity
                .ok(new ResponseData<>(
                        outcomeRepository.findByStatusFalse()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)),
                        "Outcome status : FALSE" + SUCCESSFULLY_RETRIEVED));
    }

    /**
     * This method is used to get outcome by status false value with response
     * 
     * @param status - status of outcome
     * @return outcome by status false with response
     */
    @Override
    public ResponseEntity<ResponseData<List<Outcome>>> getByStatusTrueWithResponse() {
        return ResponseEntity
                .ok(new ResponseData<>(
                        outcomeRepository.findByStatusTrue()
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)),
                        "Outcome status : TRUE" + SUCCESSFULLY_RETRIEVED));
    }

    /**
     * This method is used to get all outcome by budget id
     * 
     * @param budgetId - id of budget
     * @param keywoard - keywoard of outcome
     * @param page     - Page number
     * @param size     - Size of page
     * @return List of outcome by budget id with pagination
     */
    @Override
    public ObjectResponseData<Outcome> getAllOutcomeByBudgetId(String budgetId, String keywoard, int page, int size) {
        if (page > 0)
            page = page - 1;

        try {
            Budget budget = budgetService.getById(budgetId);
            List<Outcome> outcomes = budget.getOutcome();
            if (keywoard != null && !keywoard.isEmpty()) {
                outcomes = outcomes.stream()
                        .filter(outcoming -> outcoming.getTitle().contains(keywoard)
                                || outcoming.getDate().toString().contains(keywoard)
                                || outcoming.getStatus().equals(keywoard))
                        .collect(Collectors.toList());
            }
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, outcomes.size());
            List<Outcome> paginatedOutcomes = outcomes.subList(fromIndex, toIndex);

            Page<Outcome> pageResult = new PageImpl<>(paginatedOutcomes, PageRequest.of(page, size), outcomes.size());
            PageData pagination = paginationUtil.setPageData(page, size, outcomes.size());
            return new ObjectResponseData<>(pageResult.getContent(), pagination);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to insert new outcome
     * 
     * @param outcomeRequest - Request body of outcome
     * @return Outcome with response data
     */
    @Override
    public ResponseEntity<ResponseData<Outcome>> insertOutcome(OutcomeRequest outcomeRequest) {
        try {
            Outcome outcome = modelMapper.map(outcomeRequest, Outcome.class);
            outcome.setAmount(BigDecimal.ZERO);
            outcome.setIsDeleted(false);
            ZoneId zoneId = ZoneId.of("Asia/Jakarta");
            LocalDateTime now = LocalDateTime.now(zoneId);
            outcome.setDate(now);
            String budgetId = outcomeRequest.getBudget();
            if (budgetId == null || budgetId.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Budget id is cannot be null");
            }
            Budget budget = budgetRepository.findById(budgetId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget id is not found"));
            outcome.setBudget(budget);

            return new ResponseEntity<>(
                    new ResponseData<>(outcomeRepository.save(outcome), "Outcome" + SUCCESSFULLY_CREATED),
                    HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to connect outcome with budget by their id
     * 
     * @param outcomeId - path variable of outcome id
     * @param budgetId  - path variable of budget id
     * @return Outcome with response data
     */
    @Override
    public ResponseEntity<ResponseData<Outcome>> connectOutcomeWithBUdget(String outcomeId, String budgetId) {
        try {
            Outcome outcome = getById(outcomeId);
            Budget budget = budgetService.getById(budgetId);
            outcome.setBudget(budget);
            return new ResponseEntity<>(new ResponseData<>(outcomeRepository.save(outcome),
                    "Outcome and budget has successfully connected"), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to soft delete outcome by their id
     * 
     * @param outcomeId - path variable of outcome id
     * @return Outcome with response data
     */
    @Override
    public ResponseEntity<ResponseData<Outcome>> deleteOutcome(String outcomeId) {
        try {
            Outcome outcome = getById(outcomeId);
            outcome.setIsDeleted(true);
            outcomeRepository.save(outcome);
            return new ResponseEntity<>(
                    new ResponseData<>(outcome, "Outcome with id : " + outcomeId + SUCCESSFULLY_DELETED),
                    HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    /**
     * This method is used to minus amount total balance of budget by outcome id
     * 
     * @param outcomeId      - path variable of outcome id
     * @param outcomeRequest - request body of outcome
     * @return Outcome with response data
     */
    @Override
    public ResponseEntity<ResponseData<Outcome>> minusAmountBudgetWithOutcome(String outcomeId,
            OutcomeRequest outcomeRequest) {
        try {
            Outcome existingOutcome = getById(outcomeId);
            BigDecimal newAmount = outcomeRequest.getAmount();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Jakarta"));

            existingOutcome.setDate(now);
            existingOutcome.setTitle(outcomeRequest.getTitle());
            existingOutcome.setDescription(outcomeRequest.getDescription());
            existingOutcome.setAmount(outcomeRequest.getAmount());
            existingOutcome.setIsDeleted(false);
            existingOutcome.setStatus(outcomeRequest.getStatus());
            if (outcomeRequest.getStatus() == false) {
                return new ResponseEntity<>(
                        new ResponseData<>(existingOutcome, "Outcome is rejected because status is false"),
                        HttpStatus.BAD_REQUEST);
            }
            Outcome updatedOutcome = outcomeRepository.save(existingOutcome);
            Budget budget = updatedOutcome.getBudget();
            BigDecimal totalBalance = budget.getTotalBalance().subtract(newAmount);
            budget.setTotalBalance(totalBalance);
            budget.setDate(now);
            budgetRepository.save(budget);

            History history = new History();
            history.setDate(now);
            history.setNotes(updatedOutcome.getDescription());
            history.setAmount(newAmount);
            history.setOutcome(updatedOutcome);
            history.setBudget(budget);
            history.setType(Type.OUTCOME);
            historyRepository.save(history);

            return new ResponseEntity<>(new ResponseData<>(updatedOutcome, "Outcome has successfully completed"),
                    HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }
}
