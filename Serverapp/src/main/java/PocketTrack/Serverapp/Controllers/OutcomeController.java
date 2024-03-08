package PocketTrack.Serverapp.Controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Domains.Entities.Outcome;
import PocketTrack.Serverapp.Domains.Models.Requests.OutcomeRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Services.Interfaces.OutcomeService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("outcome")
public class OutcomeController {
    private OutcomeService outcomeService;

    @GetMapping("{id}")
    public ResponseEntity<?> getOutcomeById(@PathVariable String id) {
        return outcomeService.getByIdWithResponse(id);
    }

    @GetMapping("date/{date}")
    public ResponseEntity<?> getOutcomeByDate(@PathVariable LocalDateTime date) {
        return outcomeService.getByDateWithResponse(date);
    }

    @GetMapping("title/{title}")
    public ResponseEntity<?> getOutcomeByTitle(@PathVariable String title) {
        return outcomeService.getByTitleWithResponse(title);
    }

    @GetMapping("amount/{amount}")
    public ResponseEntity<?> getOutcomeByAmount(@PathVariable BigDecimal amount) {
        return outcomeService.getByAmountWithResponse(amount);
    }

    @GetMapping("status-false")
    public ResponseEntity<?> getOutcomeByStatusFalse() {
        return outcomeService.getByStatusFalseWithResponse();
    }

    @GetMapping("status-true")
    public ResponseEntity<?> getOutcomeByStatusTrue() {
        return outcomeService.getByStatusTrueWithResponse();
    }

    @GetMapping("outcome/{outcomeId}/budget/{budgetId}")
    public ResponseEntity<?> connectOutcomeWithBudget(@PathVariable String outcomeId, @PathVariable String budgetId) {
        return outcomeService.connectOutcomeWithBUdget(outcomeId, budgetId);
    }

    @GetMapping
    public ObjectResponseData<Outcome> getAllOutcome(@PathVariable String budgetId,
            @RequestParam(required = false) LocalDateTime date,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return getAllOutcome(budgetId, date, title, amount, page, size);
    }

    @PostMapping("insert")
    public ResponseEntity<?> insertOutcome(@RequestBody OutcomeRequest outcomeRequest) {
        return outcomeService.insertOutcome(outcomeRequest);
    }

    @DeleteMapping("{id}/delete")
    public ResponseEntity<?> deleteOutcome(@PathVariable String id) {
        return outcomeService.deleteOutcome(id);
    }

}
