package PocketTrack.Serverapp.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import PocketTrack.Serverapp.Domains.Entities.Income;
import PocketTrack.Serverapp.Domains.Models.ErrorData;
import PocketTrack.Serverapp.Domains.Models.Requests.IncomeRequest;
import PocketTrack.Serverapp.Domains.Models.Responses.ObjectResponseData;
import PocketTrack.Serverapp.Services.Interfaces.IncomeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/income")
public class IncomeController {
    private IncomeService incomeService;

    @GetMapping("{id}")
    public ResponseEntity<?> getIncomeById(@PathVariable String id) {
        return incomeService.getByIdWithResponse(id);
    }

    @GetMapping("title/{title}")
    public ResponseEntity<?> getIncomeByTitle(@PathVariable String title) {
        return incomeService.getByTitleWithResponse(title);
    }

    @PostMapping("admin/income/{incomeId}/budget/{budgetId}")
    public void connectIncomeWithBudget(@PathVariable String incomeId, @PathVariable String budgetId) {
        incomeService.connectIncomeWithBudget(incomeId, budgetId);
    }

    @GetMapping("{budgetId}/budget-income")
    public ObjectResponseData<Income> getAllIncomeByBudgetId(@PathVariable String budgetId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return incomeService.getAllIncomeByBudget(budgetId, keyword, page, size);
    }

    @PostMapping("admin/insert")
    public ResponseEntity<?> insertIncome(@Valid @RequestBody IncomeRequest incomeRequest, Errors errors) {
        if (errors.hasErrors()) {
            List<ErrorData> errorDataList = new ArrayList<>();
            errors.getAllErrors().forEach(error -> errorDataList.add(new ErrorData(error.getDefaultMessage())));
        }
        return incomeService.insertIncome(incomeRequest);
    }

    @PatchMapping("add-amount/{id}")
    public ResponseEntity<?> addAmountBudgetWithIncomeId(@PathVariable String id,
            @Valid @RequestBody IncomeRequest incomeRequest, Errors errors) {
        if (errors.hasErrors()) {
            List<ErrorData> errorDataList = new ArrayList<>();
            errors.getAllErrors().forEach(error -> errorDataList.add(new ErrorData(error.getDefaultMessage())));
        }
        return incomeService.addAmountBudgetWithIncomeId(id, incomeRequest);
    }

    @DeleteMapping("admin/delete/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable String id) {
        return incomeService.deleteWithResponse(id);
    }

}
