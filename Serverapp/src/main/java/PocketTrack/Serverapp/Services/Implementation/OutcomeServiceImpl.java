package PocketTrack.Serverapp.Services.Implementation;

import static PocketTrack.Serverapp.Domains.Constants.ServiceMessage.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import PocketTrack.Serverapp.Domains.Entities.Budget;
import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Models.PageData;
import PocketTrack.Serverapp.Domains.Models.Requests.OutcomeRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Domains.Models.Responses.ResponseData;
import PocketTrack.Serverapp.Repositories.OutcomeRepository;
import PocketTrack.Serverapp.Services.Implementation.Base.BaseServicesImpl;
import PocketTrack.Serverapp.Services.Interfaces.BudgetService;
import PocketTrack.Serverapp.Services.Interfaces.OutcomeService;
import PocketTrack.Serverapp.Utilities.GenericSpecificationsBuilder;
import PocketTrack.Serverapp.Utilities.PaginationUtil;
import PocketTrack.Serverapp.Utilities.SpecificationFactory;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class OutcomeServiceImpl extends BaseServicesImpl<Outcome, String> implements OutcomeService {

    private OutcomeRepository outcomeRepository;
    private SpecificationFactory<Outcome> outcomeSpecificationFactory;
    private PaginationUtil paginationUtil;
    private ModelMapper modelMapper;
    private BudgetService budgetService;

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
     * This method is used to get all outcome
     * 
     * @param date   - date of outcome
     * @param title  - title of outcome
     * @param amount - amount of outcome
     * @param page   - Page number
     * @param size   - Size of page
     * @return List of outcome with pagination
     */
    @Override
    public ObjectResponseData<Outcome> getAllOutcomeByBudgetId(String budgetId, LocalDateTime date, String title,
            BigDecimal amount, int page, int size) {
        if (page > 0) {
            page = page - 1;
        }
        GenericSpecificationsBuilder<Outcome> builder = new GenericSpecificationsBuilder<>();
        if (date != null) {
            builder.with(outcomeSpecificationFactory.isContain("date", date));
        }
        if (title != null) {
            builder.with(outcomeSpecificationFactory.isContain("title", title));
        }
        if (title != null) {
            builder.with(outcomeSpecificationFactory.isContain("amount", amount));
        }
        try {
            builder.with(outcomeSpecificationFactory.isEqualJoin("id", budgetId, Collections.singletonList("budget")));
            Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
            Page<Outcome> outcomePage = outcomeRepository.findAll(builder.build(), pageable);
            PageData pageData = paginationUtil.setPageData(page, size, (int) outcomePage.getTotalElements());
            return new ObjectResponseData<>(outcomePage.getContent(), pageData);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    @Override
    public ResponseEntity<ResponseData<Outcome>> insertOutcome(OutcomeRequest outcomeRequest) {
        try {
            Outcome outcome = modelMapper.map(outcomeRequest, Outcome.class);
            outcome.setAmount(BigDecimal.ZERO);
            outcome.setIsDeleted(false);
            ZoneId zoneId = ZoneId.of("Asia/Jakarta");
            LocalDateTime now = LocalDateTime.now(zoneId);
            outcome.setDate(now);

            return new ResponseEntity<>(
                    new ResponseData<>(outcomeRepository.save(outcome), "Outcome" + SUCCESSFULLY_CREATED),
                    HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

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

    @Override
    public ResponseEntity<ResponseData<Outcome>> deleteOutcome(String id) {
        try {
            Outcome outcome = getById(id);
            outcome.setIsDeleted(true);
            outcomeRepository.save(outcome);
            return new ResponseEntity<>(new ResponseData<>(outcome, "Outcome with id : " + id + SUCCESSFULLY_DELETED),
                    HttpStatus.OK);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }

    public ResponseEntity<ResponseData<Outcome>> minusAmountBudgetWithoutcome(String id,
            OutcomeRequest outcomeRequest) {
        try {
            Outcome existingOutcome = getById(id);
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
            return new ResponseEntity<>(new ResponseData<>(updatedOutcome, "Outcome has successfully completed"),
                    HttpStatus.OK);

        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(e.getStatusCode(), e.getReason());
        }
    }
}
